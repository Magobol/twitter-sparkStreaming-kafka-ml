# Twitter Sentiment Analysis of Bolivian November 2019 crisis

### Context and goals
Bolivia went through a political crisis in November 2019 after president Evo Morales resigned following violent demonstrations in the country.
Following his resignation, two sides appeared in Bolivian society: the first claiming that morales resigned following a coup, and the second claiming
that Morales resigned after organizing an electoral fraud. Both sides expressed their opinions on Twitter. In particular, pro-Morales
supporters began systematically twitting with the hashtag #GolpeDeEstadoBolivia (coup in Bolivia) and the opposing camp 
twitted using #NoHayGolpeEnBolivia.

This Project intends to:
    - Extract and correctly label a series of twits using the hashtags mentioned above
    - Train a model to identify the position of a twit related to this specific topic
    - Create a a Kafka/Spark pipeline in order to treat and evaluate twits in real time

### Extract and label twits
A dataset of 2421 twits was collected. 689 containing the hashtag #GolpeDeEstadoEnBolivia (coup in Bolivia) labeled 0
and 1733 containing the hashtag #NoHayGolpeEnBolivia (there was no coup in Bolivia) labeled 1. The class imbalance and the 
size of the sample are definitely areas of improvement. The data is stored in `./data/data.parquet` or `./data/data.csv`

### Model selection and metrics 
The stages of the pipline used in the model are Tokenization -> Stop Words removal -> Vectorization -> TF-IDF calculation  -> Feature assembler -> Logistic Regression

The performance is particularly sensitive to the minDF parameter, which determines the proportion of tweets where
a word must be present in order to be part of the vocabulary. If we set the parameter to a small value, the performance
increases. Setting minDF to small values would mean that there are words in the vocabulary that only appeared in a few 
tweets. This could help to identify patterns such as specific hashtags or mentions with valuable information for the model.
However this will increase significantly the size of the vocabulary and hence of computation time, and also the risk
of overfitting the training dataset. So we choose to put a reasonably small value to ensure that the model performance.

__Performance metrics__

- Train / Test : 80/20
- f1 score : ~0.77
- AUC score : ~0.87

### Kafka / Spark streaming

IN PROGRESS - Goal: Stream data from twitter to kafka, then process with spark streaming and predict in real time if 
a tweet is pro or anti Evo Morales using the model.

### Project setup
To run the project:
1. Clone the repository
2. Run `pip install -r requirements.txt`
3. Update the `path_to_spark` variable in the `build_and_submit.sh` bash script
4. Execute `./build_and_submit.sh Trainer` from the project folder

This will build the project with sbt and submit a Spark job.



