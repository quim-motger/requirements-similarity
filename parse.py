import json
import io

with io.open('duplicates.json', 'r', encoding='utf-8-sig') as json_file:
	parsed_reqs = []
	reqs = json.load(json_file)
	print("Nº requirements: " + str(len(reqs['requirements'])))
	print("Nº duplicates: " + str(len(reqs['dependencies'])))
	max_length = 0
	for req in reqs['requirements']:
		parsed_req = {}
		parsed_req['id'] = req['id']
		parsed_req['summary'] = req['name']
		parsed_req['description'] = req['text']
		parsed_reqs.append(parsed_req)
		if (len(req['name']) > max_length):
			max_length = len(req['name'])
	with open('duplicates-tfm.json', 'w') as f:
		json.dump(parsed_reqs, f)
		print(max_length)