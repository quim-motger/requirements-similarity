import json
import io

with io.open('not-pair-out.json', 'r', encoding='utf-8-sig') as json_file:
#with io.open('not-pair-out.json', 'r', encoding='utf-8-sig') as json_file:
	reqs = json.load(json_file)
	print("Nº requirements: " + str(len(reqs)))
	for req in reqs:
		print(req['req1Id'] + "," + req['req2Id'] + "," + str(req['score']) + ",NO")
		#print(req['req1Id'] + "," + req['req2Id'] + "," + str(req['score']) + ",NO")