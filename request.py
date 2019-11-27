import requests 
import json
import io
  
URL = "http://localhost:9000/requirements-similarity/requirement"

print("Loading files from 2009 to 2013...")
with io.open('export-2009-2013.json', 'r', encoding='utf-8-sig') as json_file_1:
	reqs1 = json.load(json_file_1)
	print("Loading files from 2014 to 2019...")
	with io.open('export-2014-2019.json', 'r', encoding='utf-8-sig') as json_file_2:
		reqs2 = json.load(json_file_2)

		reqs = []
		print("Merging arrays...")
		for req in reqs1:
			reqs.append(req)
		for req in reqs2:
			reqs.append(req)
		print("Merged")

		openreq_requirements = []
		openreq_projects = {}

		print("Building OpenReqSchema")
		for req in reqs:

			if (req['project'] in openreq_projects):
				openreq_projects[req['project']].append(req['id'])
			else:
				openreq_projects[req['project']] = [req['id']]

			new_req = {}
			new_req['id'] = req['id']
			new_req['name'] = req['summary']
			new_req['text'] = req['description']
			new_req['requirementParts'] = []

			req_priority = {'id': 'priority', 'name': req['priority']}
			req_type = {'id': 'type', 'name': req['type']}
			sep = '\n'
			req_components = {'id': 'components', 'name': sep.join(req['components'])}
			req_versions = {'id': 'versions', 'name': sep.join(req['versions'])}

			new_req['requirementParts'].append(req_priority);
			new_req['requirementParts'].append(req_type);
			new_req['requirementParts'].append(req_components);
			new_req['requirementParts'].append(req_versions);

			openreq_requirements.append(new_req)

		openreq_projects_array = []
		for key, values in openreq_projects.items():
			project = {}
			project['id'] = key
			project['specifiedRequirements'] = values
			print("Project " + key + " has " + str(len(values)))
			openreq_projects_array.append(project)

		print(len(openreq_requirements))
		schema = {}
		schema['projects'] = openreq_projects_array
		schema['requirements'] = openreq_requirements

		requests.post(URL, json = schema) 