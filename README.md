# order-book-assignment

My implementation of the CLOB is inspired by Quant Cup winning implementaion
Refer: https://gist.github.com/elan2wang/49e85e6d7e5a9b1d9ccf1c70a4425c58#file-engine-c

Also I have implemented something similar in past running in Prod. 

I like this technique because:
- Prices are usually usually very near to other offers/bids(in fact single array can be used so that you can easily determine cross prices(bid < ask),
- It helps to weed out bad prices(very far from other bunch)
- Arrays are faster(to iterate and to load in memory pages)

** The build is on maven and JDK19, to run the program
1. Package maven using mvn clean package (JAVA_HOME must be JDK 19 or above)
2. Above commad will generate a tar file in the project's target directory
3. Extract the tar file, it will show bin and lib directories
4. Inside bin, run the bash script named exchange (this also expects JDK 19 as default JRE) 
