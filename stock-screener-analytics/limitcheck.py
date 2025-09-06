import httpx
import json
import os
from dotenv import load_dotenv

load_dotenv()

api_key = os.getenv('OPENROUTER_API_KEY')

if not api_key:
    raise ValueError("OPENROUTER_API_KEY environment variable is not set")

response = httpx.get(
  url="https://openrouter.ai/api/v1/key",
  headers={
    "Authorization": f"Bearer {api_key}",
  }
)

print(f"Response code: {response.status_code}")
print(json.dumps(response.json(), indent=2))
