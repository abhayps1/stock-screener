# Environment Setup Guide

## Quick Setup Steps

### 1. Set up your API key

**Option A: Using .env file (Recommended)**
```bash
# Copy the example file
cp .env.example .env

# Edit .env file and add your actual API key
# Replace 'your_openrouter_api_key_here' with your real API key
```

**Option B: Using system environment variables**
```bash
# Windows Command Prompt
set OPENROUTER_API_KEY=your_actual_api_key_here

# Windows PowerShell
$env:OPENROUTER_API_KEY = "your_actual_api_key_here"

# Linux/macOS
export OPENROUTER_API_KEY="your_actual_api_key_here"
```

### 2. Install dependencies
```bash
pip install -r requirements.txt
```

### 3. Run the application
```bash
python app.py
```

### 4. Verify setup
- The application should start without errors
- If the API key is missing, you'll see: `ValueError: OPENROUTER_API_KEY environment variable is not set`

## Getting your OpenRouter API Key

1. Visit [OpenRouter](https://openrouter.ai)
2. Sign up for an account
3. Go to your account settings
4. Generate a new API key
5. Copy the key and use it in your environment setup

## Available Free Models on OpenRouter

As of mid-2025, OpenRouter provides several free models that you can use with this application. Here are some commonly available free models:

Auto (let OpenRouter pick the best free model) by providing this in request data.
 "model": "openrouter/auto"

### Text Models
- **mistralai/mixtral-8x7b-instruct** — fast, smart open model by Mistral
- **mistralai/mistral-7b-instruct** — lighter, quicker version of Mixtral
- **meta-llama/llama-3-8b-instruct** — compact but capable from Meta
- **openchat/openchat-3.5-0106** — fine-tuned chat model, helpful and responsive
- **nousresearch/nous-capybara-7b** — conversational model known for memory
- **gryphe/mythomax-l2-13b** — balances creativity and logic nicely
- **openrouter/auto** — automatically picks the best free model for your use case

### Vision Models (Free Tier)
- **google/gemma-3-4b-it:free**
- **google/gemma-3-12b-it:free**
- **google/gemma-3-27b-it:free**
- **meta-llama/llama-3.2-11b-vision-instruct:free**
- **mistralai/mistral-small-3.1–24b-instruct:free**

### Additional Free Models
- **mistralai/mistral-7b-instruct:free** (approx. 32K context window)
- **mistralai/mistral-nemo:free** (huge ~131K context window)
- **mistralai/mistral-small-3.2-24b-instruct:free**
- **qwen/qwen2.5-vl-32b-instruct:free**

**Note:** Model availability may change over time. Check [OpenRouter's documentation](https://openrouter.ai/docs) for the most up-to-date list.

## Security Notes

- **Never commit your actual .env file** - it's already in .gitignore
- **Never share your API key** - treat it like a password
- **Use environment variables** in production deployments
