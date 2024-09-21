# yagro-coding-challenge

## Assumptions

- Top line of workers has priority
- Conveyor is empty at the start
- Workers do not hold any items at the start
- It is unlikely that the number of lines will change
- A worker can pick up a component when holding a finished product

## TODO

- Count finished products in hands or on conveyor at the end
- Experiment with different priorities (for different types of held items)
- Don't use so many case classes?
- Extract constants (no of slots, no of steps, etc) to start args
- See if using a matrix for workers instead of tuple is more manageable

## Expected alterations

- Change number of worker slots
- Change number of required components
- Change time taken to assemble product
- Add build stages
- Change probabilities when generating new components
- Change number of items allowed in slots
- Let workers interact with the conveyor simultaneously (place a product where a component was taken)
- Change number of items workers are allowed to hold
- Let workers interact with adjacent slots
- Run indefinitely until exit signal
- Record idle time