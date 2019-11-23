import json
import io
import matplotlib.pyplot as plt
import numpy as np
from sklearn import svm
from sklearn.model_selection import ShuffleSplit

with io.open('data-analysis.json', 'r', encoding='utf-8-sig') as json_file:
	json = json.load(json_file)
	classes = json['classes']
	data = json['data']
	print(len(classes))
	duplicate_var1 = []
	duplicate_var2 = []
	duplicate_var3 = []

	not_duplicate_var1 = []
	not_duplicate_var2 = []
	not_duplicate_var3 = []

	for x in range (0, len(data)):
		if classes[x] == 1:
			duplicate_var1.append(data[x][0])
			duplicate_var2.append(data[x][1])
			duplicate_var3.append(data[x][2])
		else:
			not_duplicate_var1.append(data[x][0])
			not_duplicate_var2.append(data[x][1])
			not_duplicate_var3.append(data[x][2])

	bins = np.linspace(0, 4, 100)
	
	plt.hist(duplicate_var1, bins, alpha=0.5, label='duplicate')
	plt.hist(not_duplicate_var1, bins,alpha=0.5, label='not_duplicate')
	plt.title('Histogram with values for var1')
	#plt.show()

	plt.hist(duplicate_var2, bins,alpha=0.5, label='duplicate')
	plt.hist(not_duplicate_var2, bins,alpha=0.5, label='not_duplicate')
	plt.title('Histogram with values for var2')
	#plt.show()

	plt.hist(duplicate_var3, bins,alpha=0.5, label='duplicate')
	plt.hist(not_duplicate_var3,bins, alpha=0.5, label='not_duplicate')
	plt.title('Histogram with values for var3')
	#plt.show()

	from sklearn.model_selection import cross_val_score
	clf = svm.SVC(kernel='rbf', C=1)
	cv = ShuffleSplit(n_splits=2, test_size=1/2, random_state=0)
	scores = cross_val_score(clf, data, classes, cv=cv)
	print(scores)
	print("Accuracy: %0.2f (+/- %0.2f)" % (scores.mean(), scores.std() * 2))