import csv
import numpy as np
carriers = {}
cities = {}
rows = []
with open('hw8q4data.csv') as file:
    readCSV = csv.reader(file, delimiter=',')
    i = 0
    j = 0
    for row in readCSV:
        if not row[0] in cities:
            cities[row[0]] = i
            i = i + 1
        if not row[1] in carriers:
            carriers[row[1]] = j
            j = j + 1
        rows.append(row)
N = len(cities)
C = len(carriers)
X = []
for i in range(len(cities)):
    X.append([0 for j in range(len(carriers))])
for i in range (len(rows)):
    city = rows[i][0]
    cid = rows[i][1]
    X[cities[city]][carriers[cid]] = rows[i][2]

M = [0 for j in range(len(carriers))]
for i in range(C):
    for j in range(N):
        M[i] = M[i] + int(X[j][i])
    M[i] = M[i] / N
M = np.array(M, dtype=float)
X = np.array(X, dtype=float)
Cov = np.transpose(X).dot(X) / N - np.transpose(M).dot(M)
Corr = Cov / np.sqrt(np.transpose(np.diagonal(Cov)).dot(np.diagonal(Cov)))
       

print(Corr)