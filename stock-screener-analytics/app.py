from flask import Flask, request, jsonify
#from openai import OpenAI
import os
from dotenv import load_dotenv
from app_utility import update_all_stocks_data
from bokeh.plotting import figure
from bokeh.embed import components
from flask_cors import CORS
import requests
from app_utility import get_financial_data_for_stock

# Load environment variables from .env file
# load_dotenv()

# Initialize Flask app
app = Flask(__name__)
CORS(app)

# Get API key from environment variable
# api_key = os.getenv('OPENROUTER_API_KEY')

# if not api_key:
#    raise ValueError("OPENROUTER_API_KEY environment variable is not set")

# Initialize OpenAI client
# client = OpenAI(
#     base_url="https://openrouter.ai/api/v1",
#     api_key=api_key,
# )

# @app.route('/get-models', methods=['GET'])
# def get_models():
#     url = "https://openrouter.ai/api/v1/models"
#     headers = {
#         "Authorization": f"Bearer {api_key}"
#     }

#     try:
#         resp = requests.get(url, headers=headers)
#         resp.raise_for_status()  # raise error if request failed
#         data = resp.json()

#         # ✅ Return a proper JSON response
#         return jsonify(data), 200

#     except requests.exceptions.RequestException as e:
#         return jsonify({"error": str(e)}), 500


# @app.route("/screener-summary", methods=["GET"])
# def generate_summary():
#     try:
#         company = request.args.get("symbol", "RELIANCE")
        
#         response = client.chat.completions.create(
#             model="perplexity/llama-3.1-sonar-large-128k-online",  # 🔑 Web-enabled model
#             messages=[
#                 {
#                     "role": "system",
#                     "content": "You are a precise data extractor. You can search the web when needed and must return exact text."
#                 },
#                 {
#                     "role": "user",
#                     "content": f"Go to https://www.screener.in/company/{company}/ and extract the exact text from the About section. Return only the raw text, no summaries."
#                 }
#             ],
#         )

#         summary = response.choices[0].message["content"]
#         return jsonify({"summary": summary})

#     except Exception as e:
#         return jsonify({"error": str(e)}), 500

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
    
@app.route('/delivery-volume', methods=['GET'])
def get_delivery_volume():
    trendlyne_id = "630"  # Example Trendlyne ID for a stock
    if(get_financial_data_for_stock(trendlyne_id) != ""):
        return jsonify({'message': 'Data fetched successfully.'})
    else:
        return jsonify({'error': f'Failed to fetch data. Please try again later.'}), 500

# @app.route("/bokeh")
# def bokeh_plot():
#     p = figure(title="Bokeh Plot Example", width=400, height=400)
#     p.line([1, 2, 3, 4], [1, 4, 9, 16], line_width=2)
#     script, div = components(p)
#     # Return the script and div as a single HTML string
#     return f"{script}\n{div}"


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
