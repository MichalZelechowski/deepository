
1. Load pretrained VGG16 model from network and cache locally
1. Load content image, mix it with random noise (combination)
1. For every conv layer compute gram matrix (flat version of activations multiplicated by transposition <=> flat_activation^2?) for style
1. feedforward combination
1. backpropagate style
    * derivative loss style in layer - style gram matrix with activations on combination
    * backpropagate data from Gatys through vgg16 style layers
1. backpropagate content
    * derivative loss contet in layer - content gram matrix with activations on combination
    * backpropagate data from Gatys through vgg16  all layers
1. weight sum backpropagation output from content and style
1. apply updater straight onto these values
1. update combination with values
1. logging of  loss
    1. calculate total style loss - style and combination sum of squared errors on every style layer
    1. calculate content loss - combination and content sum of squared errors
1. reiterate