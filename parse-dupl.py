import json
import io

with io.open('not_duplicates.json', 'r', encoding='utf-8-sig') as json_file:
	parsed_reqs = []
	reqs = json.load(json_file)
	print("Nº requirements: " + str(len(reqs['requirements'])))
	print("Nº duplicates: " + str(len(reqs['dependencies'])))
	max_length = 0
	for req in reqs['dependencies']:
		parsed_req = {}
		parsed_req['fromid'] = req['fromid']
		parsed_req['toid'] = req['toid']
		parsed_req['dependency_type'] = 'duplicates'
		parsed_reqs.append(parsed_req)
	obj = {}
	obj['dependencies'] = parsed_reqs
	with open('not-duplicates-tfm.json', 'w') as f:
		json.dump(obj, f)

with io.open('duplicates.json', 'r', encoding='utf-8-sig') as json_file:
	parsed_reqs = []
	reqs = json.load(json_file)
	print("Nº requirements: " + str(len(reqs['requirements'])))
	print("Nº duplicates: " + str(len(reqs['dependencies'])))
	max_length = 0
	for req in reqs['dependencies']:
		parsed_req = {}
		parsed_req['fromid'] = req['fromid']
		parsed_req['toid'] = req['toid']
		parsed_req['dependency_type'] = 'duplicates'
		parsed_reqs.append(parsed_req)
	obj = {}
	obj['dependencies'] = parsed_reqs
	with open('duplicates-tfm.json', 'w') as f:
		json.dump(obj, f)
