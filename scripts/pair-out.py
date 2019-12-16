import json
import io

with io.open('../json-files/bm25f-results.json', 'r', encoding='utf-8-sig') as json_file:
#with io.open('not-pair-out.json', 'r', encoding='utf-8-sig') as json_file:
	reqs = json.load(json_file)
	print("Nº deps: " + str(len(reqs['dependencies'])))
	for req in reqs['dependencies']:
		if (req['status'] == 'accepted'):
			dep_type = 'YES'
		else:
			dep_type = 'NO'
		print(req['fromid'] + "," + req['toid'] + "," + str(req['dependency_score']) + "," + dep_type)
		#print(req['req1Id'] + "," + req['req2Id'] + "," + str(req['score']) + ",NO")