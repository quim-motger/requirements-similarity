import requests 
import json
import io
  
URL = "http://localhost:9000/requirements-similarity/requirement"

with io.open('export-2014-2019.json', 'r', encoding='utf-8-sig') as json_file:
	reqs = json.load(json_file)
	requests.post(URL, json = reqs) 