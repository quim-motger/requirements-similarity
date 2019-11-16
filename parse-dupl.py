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
		parsed_req['req1Id'] = req['fromid']
		parsed_req['req2Id'] = req['toid']
		parsed_req['tag'] = 'NOT_DUPLICATE'
		parsed_reqs.append(parsed_req)
	with open('not-duplicates-tfm.json', 'w') as f:
		json.dump(parsed_reqs, f)

with io.open('duplicates.json', 'r', encoding='utf-8-sig') as json_file:
	parsed_reqs = []
	reqs = json.load(json_file)
	print("Nº requirements: " + str(len(reqs['requirements'])))
	print("Nº duplicates: " + str(len(reqs['dependencies'])))
	max_length = 0
	for req in reqs['dependencies']:
		parsed_req = {}
		parsed_req['req1Id'] = req['fromid']
		parsed_req['req2Id'] = req['toid']
		parsed_req['tag'] = 'DUPLICATE'
		parsed_reqs.append(parsed_req)
	with open('duplicates-tfm.json', 'w') as f:
		json.dump(parsed_reqs, f)
