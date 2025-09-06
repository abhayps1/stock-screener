# AI Summarizer Flask API

A Flask-based API service that provides AI-powered text summarization and chat capabilities using OpenAI's API.

## Features

- **Text Summarization**: POST `/summarize` endpoint for summarizing text
- **General Chat**: POST `/chat` endpoint for general AI conversations
- **Health Check**: GET `/health` endpoint for service status

## Setup

1. Install dependencies:
```bash
pip install -r requirements.txt
```

2. Run the application:
```bash
python app.py
```

## API Endpoints

### Health Check
- **GET** `/health`
- Returns service status

### Summarize Text
- **POST** `/summarize`
- **Body**: `{"prompt": "Your text to summarize"}`
- **Response**: `{"success": true, "response": "summarized text", "model": "..."}`

### Chat
- **POST** `/chat`
- **Body**: `{"message": "Your message"}`
- **Response**: `{"success": true, "response": "AI response", "model": "..."}`

## Example Usage

```bash
# Test health endpoint
curl http://localhost:5000/health

# Summarize text
curl -X POST http://localhost:5000/summarize \
  -H "Content-Type: application/json" \
  -d '{"prompt": "What is the meaning of life?"}'

# Chat with AI
curl -X POST http://localhost:5000/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Explain quantum computing in simple terms"}'
