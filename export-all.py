import requests 
import json
import io
import csv
import datetime
import sys

def fix_nulls(s):
    for line in s:
        yield line.replace('\0', ' ')

csv.field_size_limit(sys.maxsize)

# https://bugreports.qt.io/sr/jira.issueviews:searchrequest-csv-current-fields/temp/SearchRequest.csv?jqlQuery=id+%3D+QTWEBSITE-887+OR+id+%3D+QTWEBSITE-886
BASE_URL = "https://bugreports.qt.io/sr/jira.issueviews:searchrequest-csv-all-fields/temp/SearchRequest.csv?jqlQuery=createdDate >= '"
MIDDLE_URL = "' AND createdDate <= '"
END_URL = "'"

out_reqs = []

#"2005/01/01"
#"2005/12/31"

y = 2005
m = 1
d1 = 1

while y < 2019 or (y == 2019 and m <= 10):
	t1 = datetime.datetime(y, m, d1, 0, 0)
	if (d1 == 1):
		d2 = 14
	else:
		if m == 1 or m == 3 or m == 5 or m == 7 or m == 8 or m == 10 or m == 12:
			d2 = 31
		elif m == 4 or m == 6 or m == 9 or m == 11:
			d2 = 30
		else:
			d2 = 28
	t2 = datetime.datetime(y, m, d2, 0, 0)

	url = BASE_URL + t1.strftime('%Y/%m/%d') + MIDDLE_URL + t2.strftime('%Y/%m/%d') + END_URL
	print(url)
	data = requests.get(url).content.decode('utf-8')

	cr = list(csv.reader(fix_nulls(data.splitlines()), delimiter=',', ))

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
		if (len(cr[row]) > 1):
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

			#if (len(req['description']) <= 20000):
			out_reqs.append(req)
				
		row += 1


	if (d1 == 1):
		d1 = 15
	else:
		d1 = 1
		m += 1

	if (m > 12):
		y += 1
		m = 1

with open('export-ALL.json', 'w') as f:
	print(len(out_reqs))
	json.dump(out_reqs, f)