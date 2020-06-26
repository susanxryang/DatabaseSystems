1. clustered index scan on R.a  

- X * B(R) = (250-200)/(250-150) * 1000 = 0.5 * 1000 = 500

2. block-at-a-time nested loop join on R.a = S.a  

- B(s) + T(s)(B(r)/V(a, R)) = 2000 + 4 * 10^5 = 402000


3. unclustered nested loop join S.b = U.b  

- We can use either B(RS) + B(RS) * B(U) or B(U) + B(U) * B(RS)
- Then since B(RS) is an intermediate step, we don't need to read to from memory for this step of 
the computation, this makes the the computation represented by first equation faster. 
So we first estimate B(RS) = B(R) * B(S)/max{V(a, R), V(a, S)} = 500 * 2000/max{50, 250} = 500*2000/250 = 4000
Then we have B(RS) * B(U) = 4000 * 500 = 2000000 = 2 * 10^6.


**Total Cost :**
Total I/O cost = 500 + 402000 + 2000000 = 2402500