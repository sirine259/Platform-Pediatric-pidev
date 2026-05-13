export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp?: string;
}

export interface ChatRequest {
  message: string;
  sessionId: string;
  category?: string;
}

export interface ChatResponse {
  message: string;
  sessionId: string;
  timestamp: string;
  success: boolean;
  error?: string;
}
