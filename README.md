# deepository

This repository contains set of subprojects, that are related to my experimentations with deep learning. All current projects
are based on [deeplearning4j](https://deeplearning4j.org/)

# workbench

It's a project that contains components, that help conducting and evaluating experiments.

# lego-builder

The idea of this project is to learn neural network how to create buildings out of lego bricks. My intention was to get deeper
knowledge about RNNs architetures and evaluate if it can be useful in discovering rules that are sufficient to build correctly 
constructed buildings. I expected to train model that is capable of constructions that it has never seen before.

# lego-color-transfer

It's just a clone of deeplearning4j example of neural style transfer between 2 images. The content is rendered castle and the style is building generated
by the network trained in `lego-builder` project.

# lego-mnist

Simple bricks classifier (in progress).