# lego-builder

## Problem definition

Given an unrestrited set of straight bricks of different lengths and 1 unit height and width, I would like to have
a model that, when asked, generates a building, that matches following conditions:
 * it consists of at least one outline
 * all outlines are of the same shape, likely rectangular
 * outlines are stacked on each other
 * was not seen previously by the model
> TODO add picture of the building here

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
 
## Starting with the small train set

Following hints from more [experienced](https://blog.slavv.com/37-reasons-why-your-neural-network-is-not-working-4020854bd607)
I decided to start trianing with a very small set of 10 buildings and very small network. I was able to train this network quickly, but also overfitted to this small input set. Such network was able to only generate exact same buildings that it has
seen before.
> TODO insert loss chart, with epochs information, and % of generated building

## Increasing train set

The next train set contained 500 buildings. It took considerably longer time to find suitable network configuration and train it to sensible loss value. 
> TODO insert loss chart here
Network generates correct building almost every time asked, but only small fraction is unique.
> TODO fraction put here
> TODO tell about configuration tried and problems with vanishing & exploding gradients, tbptt length, variations with number of layers, batch size etc.

## Final train set

The final train set size was 25,000 buildings. Due to the limited computation capacity only limited number of epochs was trained.
> TODO chart again
Loss value converged quite soon, at the high value, however network started exposing interesting capabilities. It was generating correct building in 50% of the cases and almost all of them had never been seen by the model.
> TODO numbers, numbers, numbers!
