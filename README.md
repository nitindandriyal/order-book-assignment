# order-book-assignment

My implementation of the CLOB is inspired by Quant Cup winning implementaion(Refer: https://web.archive.org/web/20141222151051/https://dl.dropboxusercontent.com/u/3001534/engine.c)

Also I have implemented something similar in past running in Prod. I like this technique because prices are usually usually very near to other 
offers/bids(in fact single array can be used so that you can easily determine cross prices(bid < ask), Also it helps to weed out bad prices(very far from other bunch)
