import { Component, OnInit } from '@angular/core';
import { ChatbotService } from '../../../services/chatbot.service';
import { ChatMessage } from '../../../models/chatbot.model';

@Component({
  selector: 'app-chatbot',
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.css']
})
export class ChatbotComponent implements OnInit {
  messages: ChatMessage[] = [];
  userMessage: string = '';
  isLoading: boolean = false;
  sessionId: string = '';
  isOpen: boolean = false;
  isTyping: boolean = false;

  quickQuestions = [
    'Qu\'est-ce que la dialyse?',
    'Comment se déroule une greffe rénale?',
    'Quelle nutrition pour mon enfant?',
    'Quels sont les symptômes?'
  ];

  constructor(private chatbotService: ChatbotService) {}

  ngOnInit(): void {
    this.sessionId = localStorage.getItem('chatbotSessionId') || this.generateSessionId();
    localStorage.setItem('chatbotSessionId', this.sessionId);
    this.loadChatHistory();
  }

  private generateSessionId(): string {
    return 'session_' + Date.now() + '_' + Math.random().toString(36).substring(7);
  }

  private loadChatHistory(): void {
    this.chatbotService.getChatHistory(this.sessionId).subscribe({
      next: (history) => {
        if (history && history.length > 0) {
          this.messages = history.map(msg => ({
            role: 'user' as const,
            content: msg.userMessage,
            timestamp: msg.timestamp
          }));
          history.forEach(msg => {
            this.messages.push({
              role: 'assistant' as const,
              content: msg.botResponse,
              timestamp: msg.timestamp
            });
          });
        }
      },
      error: () => {
        this.messages = [{
          role: 'assistant',
          content: 'Bonjour ! Je suis NephroBot, votre assistant en néphrologie pédiatrique. Comment puis-je vous aider aujourd\'hui ?',
          timestamp: new Date().toISOString()
        }];
      }
    });
  }

  sendMessage(): void {
    if (!this.userMessage.trim() || this.isLoading) return;

    const message = this.userMessage;
    this.messages.push({
      role: 'user',
      content: message,
      timestamp: new Date().toISOString()
    });
    this.userMessage = '';
    this.isLoading = true;
    this.isTyping = true;

    this.chatbotService.sendMessage({
      message: message,
      sessionId: this.sessionId,
      category: 'nephrologie'
    }).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.isTyping = false;
        if (response.success) {
          this.messages.push({
            role: 'assistant',
            content: response.message,
            timestamp: response.timestamp
          });
        } else {
          this.messages.push({
            role: 'assistant',
            content: 'Désolé, une erreur est survenue: ' + response.error,
            timestamp: new Date().toISOString()
          });
        }
      },
      error: () => {
        this.isLoading = false;
        this.isTyping = false;
        this.messages.push({
          role: 'assistant',
          content: 'Désolé, je ne peux pas répondre pour le moment. Veuillez réessayer.',
          timestamp: new Date().toISOString()
        });
      }
    });
  }

  askQuickQuestion(question: string): void {
    this.userMessage = question;
    this.sendMessage();
  }

  clearChat(): void {
    this.chatbotService.clearChatHistory(this.sessionId).subscribe({
      next: () => {
        this.messages = [{
          role: 'assistant',
          content: 'Chat effacé ! Comment puis-je vous aider ?',
          timestamp: new Date().toISOString()
        }];
      }
    });
  }

  toggleChat(): void {
    this.isOpen = !this.isOpen;
  }

  formatTime(timestamp?: string): string {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
  }
}
