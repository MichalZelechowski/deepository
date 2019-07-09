# lego-builder

## Problem definition

Given an unrestrited set of straight bricks of different lengths and 1 unit height and width, I would like to have
a model that, when asked, generates a building, that matches following conditions:
 * it consists of at least one outline
 * all outlines are of the same shape, likely rectangular
 * outlines are stacked on each other
 * was not seen previously by the model
 
> Note: as the conditions are fabricated, it's easy to solve the problem with random generator and set of building rules, however 
the intention was to learn RNN architecture and deep networks in more details, given some simple problem

## Data at hand

As I had no list of such buildings I decided to generate it. There's a generator that for the rectangular shape of given maximum
wall lengths generates set of correctly constructed buildings.
