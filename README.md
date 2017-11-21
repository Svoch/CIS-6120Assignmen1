# LearnAC

Java implementation of an arithmetic circuits learner. This algorithm is similar to a Bayesian network learner with context-specific independence by greedily splitting conditional distributions, at each step scoring the candidates by compiling the resulting network into an arithmetic circuit, and using its size as the penalty.

The algorithm is based on "Learning Arithmetic Circuits" paper by Daniel Lowd and Pedro, Domingos Department of Computer Science and Engineering, University of Washington, Seattle, USA.