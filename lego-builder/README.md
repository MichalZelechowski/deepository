# lego-builder

## Problem definition

Given an unrestrited set of straight bricks of different lengths and 1 unit height and width, I would like to have
a model that, when asked, generates a building, that matches following conditions:
 * it consists of at least one outline
 * all outlines are of the same shape, likely rectangular
 * outlines are stacked on each other
 * was not seen previously by the model
 
 ![sample_building](img/sample_building.png)

> Note: as the conditions are fabricated, it's easy to solve the problem with random generator and set of building rules, however 
the intention was to learn RNN architecture and deep networks in more details, given some simple problem

## Data at hand

As I had no list of such buildings I decided to generate it. There's a generator that for the rectangular shape of given maximum
wall lengths generates set of correctly constructed buildings.

Every building consists of outlines and every outline cosists of bricks. Brick holds information about:
 * position in 3D space
 * length
 * direction (4 possible values: north, east, west, south)
 
 There's domain layer that represents bricks and outlines in object oriented way. In order to use such data as an input to the model, is encoded in the following way:
 ```
 brickEncoded = 100 + brick.length * 4 + directionToInteger(brick.direction)
 ```
 As an example `Brick(3, NORTH)` is `113` and `Brick(1, WEST)` is `106`. When put in sequence, `113, 106, 110` gives `Brick(<0, 0, 0>, 3, NORTH), Brick(<0, 0, 3>, 1, WEST), Brick(<-1, 0, 3>, 2, WEST)`.
 
 There are 3 special values:
 * 0 - next outline, move one level (y) up
 * 1 - end of the building, no more outlines out there
 * -1 - nothing
 
## Selecting network architecture

I picked [RNN](https://www.coursera.org/lecture/nlp-sequence-models/recurrent-neural-network-model-ftkzt) to tackle the problem. Encoded building was treated as a list of discrete values that was transformed with one-hot-encoding and passed as a sequence into network input layer. For every brick, the next in sequence was given as a label.

Network consisted of set of [LSTM layers](https://www.coursera.org/lecture/nlp-sequence-models/long-short-term-memory-lstm-KXoay). I followed [RNN hints](https://towardsdatascience.com/rnn-training-tips-and-tricks-2bf687e67527) and was adapting size of the network to the input train sets sizes.

> TODO differen way of iterating over data (paddingvs no padding, etc.)

## Training model

As one building was encoded on average to 100 values then 1,000 of buildings encoded to 100,000 values. If compared to sentences in classical [sampling of novel sequences problem](https://www.coursera.org/lecture/nlp-sequence-models/sampling-novel-sequences-MACos), setting up 1,000 buildings could be compared to 55 page novel.

Following [hints regarding size](https://towardsdatascience.com/rnn-training-tips-and-tricks-2bf687e67527), I adapted size of the layers to training set size.

> TODO tell about environment conditions, hardware, memory consumption etc.
 
### Starting with the small train set

In order to avoid [common RNN pitfalls](https://blog.slavv.com/37-reasons-why-your-neural-network-is-not-working-4020854bd607)
I decided to start training with a very small set of 10 buildings and very small network. I was able to train this network quickly, but also overfitted to this small input set. Such network was able to only generate exact same buildings that it has
seen before.

> TODO insert loss chart, with epochs information, and % of generated building

### Increasing train set size

The next train set contained 500 buildings. It took considerably longer time to find suitable network configuration and train it to sensible loss value.

The configuration was about 2 LSTM layers followed by RNN output layer with softmax activation. 

```java
MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .cacheMode(CacheMode.DEVICE)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.0005))
                .list()
                .layer(0, new LSTM.Builder().nIn(27).nOut(128)
                        .activation(Activation.TANH).build())
                .layer(1, new LSTM.Builder().nIn(128).nOut(128)
                        .activation(Activation.TANH).build())
                .layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).activation(Activation.SOFTMAX)
                        .nIn(128).nOut(27)
                        .build())
                .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(220).tBPTTBackwardLength(220)
                .build();
```

There were nearly 220,000 network parameters and the training set was about 100,000 characters, so it felt like a sufficient number to conduct the training. 

![500_score](img/500_score.png)

Although model score had nice slope, there were some peeks that could mean:
 * too big learning rate
 * instability in computation causing exploding gradients
 * loss function having very sharp edges
 
 ![500_updates](img/500_update_ratios.png)
 
High variation in update of the parameters could mean instabilities, but zooming into the very last epochs gave strong suspicion that from time to time updater steps out from the local minima due to the not well adapted learning rate and falls back to it after some iterations.

![500_score_finish](img/500_score_finish.png) | ![500_updates_finish](img/500_update_ratios_finish.png)

Reducing learning rate though resulted in convergence happening too soon.

Network generates correct building almost every time asked, but only small fraction is unique.

> TODO fraction put here

> TODO tell about configuration tried and problems with vanishing & exploding gradients, tbptt length, variations with number of layers, batch size etc.

### Final train set

The final train set size was 25,000 buildings. The network configuration was as following:

```java
MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .cacheMode(CacheMode.DEVICE)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.0005))
                .list()
                .layer(0, new LSTM.Builder().nIn(27).nOut(512)
                        .activation(Activation.TANH).build())
                .layer(1, new LSTM.Builder().nIn(512).nOut(256)
                        .activation(Activation.TANH).build())
                .layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).activation(Activation.SOFTMAX)
                        .nIn(256).nOut(27)
                        .build())
                .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(220).tBPTTBackwardLength(220)
                .build();
```

![25000_score](img/25000_score.png) | ![25000_updates](img/25000_updates.png)

Loss value converged quite soon, at the fairly high value. Also parameter update ratios was not optimal, however network started exposing interesting capabilities. It was generating correct building in 50% of the cases and almost all of them had never been seen by the model.

> TODO numbers, numbers, numbers!

## Summary & conclusions

It was interesting to see that network not only learned to create outlines of correct shape, but also knows how to create another levels of building of the same shape.
