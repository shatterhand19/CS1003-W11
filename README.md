I was asked to make a program that using hadoop extracted some information from Tweeter data.

I implemented the practical in two parts – first the basic deliverable and then the extensions.
For the basic deliverable I was using the example code as guide and changed the mapper and reducer in the appropriate manner. 
For the mapper I used a JsonReader in order to read the json object and get the hashtags from them; these I find in the hashtags arrays in the entitites object. As there are some unexpected characters (delete character in the 1-minute data) I have created a try-catch block for a JsonException which prints to the console the error message. 
For the reducer and main classes I am using basically the same code – the reducer sums the counts for all keys and outputs them and the main class creates the job and runs it.
