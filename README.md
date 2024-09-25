# yagro-coding-challenge

## Assumptions

- Conveyor is empty at the start
- Workers do not hold any items at the start
- Prefer moving components to workers not holding products
- A worker can pick up a component when holding a finished product (but cannot then replace it with a product)

## TODO

- Don't use so many case classes?

## Expected alterations

- Change number of worker slots
- Change number of required components (and change number of items workers are allowed to hold, or add build stages)
- Change time taken to assemble product
- Change probabilities when generating new components
- Change number of items allowed in slots
- Let workers interact with the conveyor twice (pick up a component and place a product)
- Let workers interact with the conveyor simultaneously (place a product where a component was taken)
- Let workers interact with adjacent slots
- Run indefinitely until exit signal
- Record idle time