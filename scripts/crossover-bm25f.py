from PIL import Image,ImageMath
import numpy as np
import os
import json
import io

from skimage.filters import threshold_otsu
from sklearn.metrics import confusion_matrix

from sklearn.cluster import KMeans

from sklearn.model_selection import train_test_split,cross_val_predict
from sklearn import datasets
from sklearn import svm, linear_model


#Obtenemos el threshold de Otsu
with io.open('../json-files/bm25f-results.json', 'r', encoding='utf-8-sig') as json_file:
	reqs = json.load(json_file)
	scores = []
	classes = []
	for duplicate in reqs['dependencies']:
		if (duplicate['dependency_score'] != None):
			scores.append(duplicate['dependency_score'])
			if (duplicate['status'] == 'accepted'):
				classes.append(1)
			else:
				classes.append(0)

	I = np.array(scores)
	I_classes = np.array(classes)
	
	I = I.reshape(-1, 1)
	I_classes = I_classes.reshape(-1, 1)

	clf = svm.SVC(C = 1)

	y_pred = cross_val_predict(clf, I, I_classes, cv=10)

	classes = y_pred >= 0.5

	tp = 0
	tn = 0
	fp = 0
	fn = 0

	i = 0
	while (i < len(classes)):
		if (classes[i] and reqs['dependencies'][i]['status'] == 'accepted'):
			tp += 1
		elif (classes[i] and reqs['dependencies'][i]['status'] == 'rejected'):
			fp += 1
		elif (not classes[i] and reqs['dependencies'][i]['status'] == 'accepted'):
			fn += 1
		elif (not classes[i] and reqs['dependencies'][i]['status'] == 'rejected'):
			tn += 1
		i += 1

	print("TP = ", tp)
	print("FP = ", fp)
	print("FN = ", fn)
	print("TN = ", tn)