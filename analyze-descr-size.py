import requests 
import json
import io
import csv
import numpy as np
import matplotlib.pyplot as plt

with io.open('export-test.json', 'r', encoding='utf-8-sig') as json_file:
	reqs = json.load(json_file)
	length = []
	for req in reqs:
		length.append(len(req['description']))

	plt.hist(length, bins='auto')
	plt.title('Histogram with description length')
	plt.show()