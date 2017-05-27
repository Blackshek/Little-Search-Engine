# Little-Search-Engine
>A project to simulate a search engine
## Background: 
This program gathers index keywords from plain text documents and stores them using a hashmap.
 These keywords can then be searched and all documents containing the words will be returned. 
 The purpose fo this project was to gain experience working with HashMaps.
##### Resources:
This project uses several plain text documents as it draws the words it stores from these documents. Specifically included here are four documents: AliceCh1.txt, WoWCh1.txt, docs.txt, and noisewords.txt.

AliceCh1.txt and WoWCh1.txt are the first chapters from Alice's Adventures in Wonderland by Lewis Carroll and War of the Worlds by H.G. Wells.

The document docs.txt simply stores the names of the two plain text documents listed above.

The last document, noisewords.txt, stores various noise words that are common and not likely to be queried.
##### Usage:
The program is run with a built in main method tester. There is no driver. The top5search method takes two keywords as parameters and returns the names of the documents in which either keyword 1 or keyword 2 occur, arranged in descending order of frequencies.
##### Algorithm Steps:
1. First two hashmaps are instantiated: the keywordsIndex and noisewords.
```java
keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
noiseWords = new HashMap<String,String>(100,2.0f);
```
2. Then all of the words in the noisewords input document are indexed in the noiseWords HashMap. The other keywords from the input 
documents are indexed in their individual HashMaps and then these HashMaps are then merged together into the global keywordsIndex HashMap. 
3. The keywordsIndex HashMap is then completed and can be searched. The top5search method takes two strings as parameters and returns the list of documents that contain the two string arguments.

##### License:
(c) Dunbar Paul Birnie IV 2017
