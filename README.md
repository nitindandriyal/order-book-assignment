# order-book-assignment

My implementation of the CLOB is inspired by Quant Cup winning implementaion
Refer: https://gist.github.com/elan2wang/49e85e6d7e5a9b1d9ccf1c70a4425c58#file-engine-c

Also I have implemented something similar in past running in Prod. 

I like this technique because:
- Prices are usually usually very near to other offers/bids(in fact single array can be used so that you can easily determine cross prices(bid < ask),
- It helps to weed out bad prices(very far from other bunch)
- Arrays are faster(to iterate and to load in memory pages)

