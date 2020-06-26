import csv
cities = {}
matches = []
with open('citiesdata.csv') as file:
    readCSV = csv.reader(file, delimiter=',')
    i = -1
    for row in readCSV:
        if i != -1:
            if not row[0] in cities:
                cities[row[0]] = i
                i = i + 1
            if not row[1] in cities:
                cities[row[1]] = i
                i = i + 1
            matches.append(row)
        else:
            i = i + 1

matrix = []
for i in range (len(cities)):
    matrix.append([0 for j in range(len(cities))])

for i in range (len(matches)):
    city1 = matches[i][0]
    city2 = matches[i][1]
    matrix[cities[city1]][cities[city2]] = 1
    matrix[cities[city2]][cities[city1]] = 1

D = [0 for i in range(len(matrix))]
for i in range(len(matrix)):
    for j in range(len(matrix[i])):
        D[i] = D[i] + matrix[i][j]

N = len(cities)
V = []
V.append([0 for i in range(N)])
for c in range(N):
    V[0][c] = 1 / N
i = 0

for c in range(N):
    # V.append([0 for i in range(N)])
    V[i][c] = 0.1 / N
    for d in range(N):
        V[i][c] = V[i][c] + 0.9 * matrix[c][d] * V[i-1][d] / D[d]
diff = [0 for k in range(len(V[0]))]
for k in range(len(V[0])):
    diff[k] = V[i][k] - V[i - 1][k]
while(sum(diff) > 0.0001):
    i = i + 1
    for c in range(N):
        # V.append([0 for i in range(N)])
        V[i][c] = 0.1 / N
        for d in range(N):
            V[i][c] = V[i][c] + 0.9 * matrix[c][d] * V[i-1][d] / D[d]

    
print(V)