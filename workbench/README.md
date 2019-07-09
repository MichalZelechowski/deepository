# workbench

## Experiment

The core of the workbench is `org.mz.deepository.workbench.Experiment` class. The idea behind is to run experiment for a given number of epochs. Object of this class, apart from training of the model, takes care about other side tasks, like checkpoint saving, progress tracing, test set evaluations, detection if training was previously paused and is being continued, monitoring.

## Train model locally

In order to train model on local machine, the following information has to be provied:
 * `modelName` - every model represents some idea of how problem should be solved; this value distinguishes ideas among each other and groups models
 * `version` - there may be different variations of the same idea of problem solution and this parameter distinguishes these ideas within the `modelName`
 * `configurationProvider` - this is the recipe for the model
 * `trainData`, `testData` - iterators over the train and test data set
 * `rootPath` - location at local drive the experiment details are written to; this is appended by `modelName` and `version`
 * `listeners` - optional list of listeners to get feedback from the training
 
In order to train model locally:
```java
LocalExperiment experiment = new LocalExperiment(
                "Rnn",
                "1.2",
                new FQN(512, 256, 16),
                Providers.of(trainDataIterator), Providers.of(testDataIterator),
                "/data/experiments",
                new WeightListener());

experiment.run(10);

experiment.shutdown();
```

Above code will create model configuration files in `/data/experiments/Rnn/1.2`. It will create neural network based on `FQN` definition and run 10 epochs ofr training based on the provided iterator. While training, `WeightListener` instance will get notifications about progress of the training.

When executing `experiment.run(1)` again, it will pick up last checkpoint of the model, reinstantiate all state related to model parameters trained so far and continue experiment with given train data.

During training, deeplearning4j web console is active, until experiment is shutdown.
