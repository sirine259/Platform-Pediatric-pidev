package com.esprit.platformepediatricback.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    @Value("${app.video.upload-dir:videos}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex);
            }

            String fileName = UUID.randomUUID() + extension;
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Absolute URL so the frontend can directly use it.
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/videos/")
                    .path(fileName)
                    .toUriString();

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("fileName", fileName);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<StreamingResponseBody> getVideo(
            @PathVariable String fileName,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader
    ) throws IOException {
        Path filePath = Paths.get(uploadDir).toAbsolutePath().resolve(fileName).normalize();
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build();
        }

        long fileSize = Files.size(filePath);
        MediaType mediaType = MediaTypeFactory.getMediaType(filePath.getFileName().toString())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        // Default: full content
        long rangeStart = 0;
        long rangeEnd = fileSize - 1;
        boolean isPartial = false;

        // Parse "Range: bytes=start-end"
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String rangeValue = rangeHeader.substring("bytes=".length()).trim();
            String[] parts = rangeValue.split("-", 2);
            try {
                if (!parts[0].isBlank()) {
                    rangeStart = Long.parseLong(parts[0]);
                }
                if (parts.length > 1 && !parts[1].isBlank()) {
                    rangeEnd = Long.parseLong(parts[1]);
                }
                if (rangeEnd >= fileSize) {
                    rangeEnd = fileSize - 1;
                }
                if (rangeStart < 0) {
                    rangeStart = 0;
                }
                if (rangeStart <= rangeEnd) {
                    isPartial = true;
                } else {
                    rangeStart = 0;
                    rangeEnd = fileSize - 1;
                }
            } catch (NumberFormatException ignored) {
                rangeStart = 0;
                rangeEnd = fileSize - 1;
                isPartial = false;
            }
        }

        long contentLength = rangeEnd - rangeStart + 1;

        HttpStatus status = isPartial ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        headers.setContentLength(contentLength);
        if (isPartial) {
            headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize);
        }

        long finalRangeStart = rangeStart;
        long finalRangeEnd = rangeEnd;

        StreamingResponseBody stream = outputStream -> {
            try (var inputStream = Files.newInputStream(filePath)) {
                // Skip to start
                long skipped = 0;
                while (skipped < finalRangeStart) {
                    long s = inputStream.skip(finalRangeStart - skipped);
                    if (s <= 0) break;
                    skipped += s;
                }

                byte[] buffer = new byte[8192];
                long remaining = finalRangeEnd - finalRangeStart + 1;
                while (remaining > 0) {
                    int read = inputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                    if (read == -1) break;
                    outputStream.write(buffer, 0, read);
                    remaining -= read;
                }
                outputStream.flush();
            }
        };

        return new ResponseEntity<>(stream, headers, status);
    }
}

