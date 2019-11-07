import requests 
import json
import io
import csv

# https://bugreports.qt.io/sr/jira.issueviews:searchrequest-csv-current-fields/temp/SearchRequest.csv?jqlQuery=id+%3D+QTWEBSITE-887+OR+id+%3D+QTWEBSITE-886
BASE_URL = "https://bugreports.qt.io/sr/jira.issueviews:searchrequest-csv-all-fields/temp/SearchRequest.csv?jqlQuery="
ID = "id+%3D+"
OR = "+OR+"

with io.open('duplicates.json', 'r', encoding='utf-8-sig') as json_file:
	reqs = json.load(json_file)['requirements']
	i = 0
	out_reqs = []
	while i < len(reqs):
		url = BASE_URL + ID + reqs[i]['id']
		i += 1
		while i % 100 != 0 and i < len(reqs):
			url += OR + ID + reqs[i]['id']
			i += 1
		print("Exporting " + str(i) + "...")
		data = requests.get(url).content.decode('utf-8')
		cr = list(csv.reader(data.splitlines(), delimiter=','))

		key_index = 0
		summary_index = 0
		description_index = 0
		priority_index = 0
		project_index = 0
		component_indexs = []
		version_indexs = []
		type_index = 0
		project_index = 0

		index = 0
		for field in cr[0]:
			if (field == 'Issue key'):
				key_index = index
			elif (field == 'Summary'):
				summary_index = index
			elif (field == 'Description'):
				description_index = index
			elif (field == 'Priority'):
				priority_index = index
			elif (field == 'Component/s'):
				component_indexs.append(index)
			elif (field == 'Affects Version/s'):
				version_indexs.append(index)
			elif (field == 'Issue Type'):
				type_index = index
			elif (field == 'Project key'):
				project_index = index
			index += 1

		row = 1
		while row < len(cr):
			req = {}
			req['id'] = cr[row][key_index]
			req['summary'] = cr[row][summary_index]
			req['description'] = cr[row][description_index]
			req['priority'] = cr[row][priority_index]
			req['type'] = cr[row][type_index]
			req['project'] = cr[row][project_index]

			components = []
			for component_index in component_indexs:
				if (cr[row][component_index] != ""):
					components.append(cr[row][component_index])
			req['components'] = components

			versions = []
			for version_index in version_indexs:
				if (cr[row][version_index] != ""):
					versions.append(cr[row][version_index])
			req['versions'] = versions

			out_reqs.append(req)
			row += 1

	with open('export.json', 'w') as f:
		json.dump(out_reqs, f)