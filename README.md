#Twitter Sentiment Analytics on Bolivian November 2019 crisis

### Context and goals
Bolivia went through a political crisis in November 2019 after president Evo Morales resigned following violent demonstrations in the country.
Following his resignal, two sides appeared in Bolivian society: the first affirming that morales resigned following a coup, and the second affirming
that Morales resigned after organizing an electoral fraud. Both sides expressed their opinions on Twitter. In particular, pro-Morales
supporters began systematically twitting with the hashtag #GolpeDeEstadoEnBolivia (coup in Bolivia) and the faction opposed to Morales
twitted using #NoHayGolpeEnBolivia.

This Project intends to
    - Extract and correctly label a series of twits using the corresponding hashtags
    - Train a model to identify the position of a twit related to this specific topic
    - Create a a Kafka/Spark pipeline in order to treat and evaluate twits as they come

### Extract and label twits
A dataset of 2421 twits was collected. 689 containing the hashtag #GolpeDeEstadoEnBolivia (coup in Bolivia) labeled 0
and 1733 containing the hashtag #NoHayGolpeEnBolivia (there was no coup in Bolivia) labeled 1. The class imbalance and the 
size of the sample are definitely areas of improvement. The data is stored in `./data/data.parquet` or `./data/data.csv`

### Model selection and metrics 
The technique used in this project is 

### Kafka / Spark streaming

IN PROGRESS


