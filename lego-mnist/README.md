# lego-mnist

Idea of this project was inspired by well known [MNIST problem](https://en.wikipedia.org/wiki/MNIST_database). The goal is to have solution, that
is given image of lego brick and tells what kind of brick is that.

For simplification I will use only cubelike bricks of `nx1x1` dimension. That should be fairly simple problem for starter project with image recognition,
 as bricks are of simple, regular shape.

## Data preparation

As collecting, taking and preparing thousands of pictures of bricks is a tedious task I will aid myself with data generation. I will use 3D software
to generate bricks of arbitrary dimensions, selected range of colors, different locations and rotations in space. That gives me labeling out of the box.
For a start I will use solid background, that's not observed in nature, and check first how such simplification works with real images.

I will use input images of size `512x512` as this size should be sufficiently clear for human expert to tell what kind of brick are we dealing with.

## Train / dev / test sets

I am splitting train / dev / test sets to 96% / 2% / 2% of the data.

## Picking the metric

I don't have any preferences to pick recall or precision, so decided to start with F1-score. There's no running time constraint for the model. I assume
that human level performance is around 95% (based on the poor lighting or small image) and I am aiming first at that performance. [Bayes optimal 
performance](https://en.wikipedia.org/wiki/Bayes_error_rate) is assumed at level 99,5%. 

