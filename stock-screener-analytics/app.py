from flask import Flask, request, jsonify
from openai import OpenAI
import os
from dotenv import load_dotenv
from add_all_stocks import update_all_stocks_data

# Load environment variables from .env file
load_dotenv()

# Initialize Flask app
app = Flask(__name__)

# Get API key from environment variable
api_key = os.getenv('OPENROUTER_API_KEY')

if not api_key:
    raise ValueError("OPENROUTER_API_KEY environment variable is not set")

# Initialize OpenAI client
client = OpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key=api_key,
)

@app.route("/generate-summary", methods=["POST"])
def generate_summary():
    try:
        
        prompts = os.getenv('PROMPTS')
        
        # Call OpenRouter AI
        response = client.chat.completions.create(
            model="openai/gpt-4o-mini",  # You can change the model if needed
            messages=[
                {"role": "system", "content": "You are an AI that generates useful summaries."},
                {"role": "user", "content": prompts},
            ],
        )

        print(response)
        # print(f"Response code: {response.status_code}")
        summary = response.choices[0].message["content"]
        return jsonify({"summary": summary})

    except Exception as e:
        return jsonify({"error is here": str(e)}), 500

# @app.route('/health', methods=['GET'])
# def health_check():
#     """Health check endpoint"""
#     return jsonify({"status": "healthy", "service": "ai-summarizer"})

# @app.route('/summarize', methods=['POST'])
# def summarize():
#     """
#     Controller function to handle AI summarization requests
#     Expects JSON payload: {"prompt": "Your text to summarize"}
#     """
#     try:
#         # Get prompt from request body
#         data = request.get_json()
#         if not data or 'prompt' not in data:
#             return jsonify({"error": "Missing 'prompt' in request body"}), 400
        
#         prompt = data['prompt']
        
#         # Call OpenAI API
#         completion = client.chat.completions.create(
#             extra_headers={
#                 "HTTP-Referer": "https://ai-summarizer.local",
#                 "X-Title": "AI Summarizer",
#             },
#             extra_body={},
#             model="deepseek/deepseek-chat-v3-0324:free",
#             messages=[
#                 {
#                     "role": "user",
#                     "content": prompt
#                 }
#             ]
#         )
        
#         # Extract response
#         response_content = completion.choices[0].message.content
        
#         return jsonify({
#             "success": True,
#             "response": response_content,
#             "model": "deepseek/deepseek-chat-v3-0324:free"
#         })
        
#     except Exception as e:
#         return jsonify({
#             "success": False,
#             "error": str(e)
#         }), 500

# @app.route('/chat', methods=['POST'])
# def chat():
#     """
#     Controller function for general chat with the AI
#     Expects JSON payload: {"message": "Your message"}
#     """
#     try:
#         data = request.get_json()
#         if not data or 'message' not in data:
#             return jsonify({"error": "Missing 'message' in request body"}), 400
        
#         message = data['message']
        
#         completion = client.chat.completions.create(
#             extra_headers={
#                 "HTTP-Referer": "https://ai-summarizer.local",
#                 "X-Title": "AI Summarizer",
#             },
#             extra_body={},
#             model="deepseek/deepseek-chat-v3-0324:free",
#             messages=[
#                 {
#                     "role": "user",
#                     "content": message
#                 }
#             ]
#         )
        
#         response_content = completion.choices[0].message.content
        
#         return jsonify({
#             "success": True,
#             "response": response_content,
#             "model": "deepseek/deepseek-chat-v3-0324:free"
#         })
        
#     except Exception as e:
#         return jsonify({
#             "success": False,
#             "error": str(e)
#         }), 500


@app.route('/update-all', methods=['POST'])
def update_all_stocks():
    """
    Endpoint to trigger the update of all stocks data
    """
    try:
        update_all_stocks_data()
        return jsonify({"success": True, "message": "All stocks data updated successfully."})
    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
