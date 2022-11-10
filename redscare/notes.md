# Project Notes

The following problems can be solved:
 - None (Done)
 - Alternate (Done)
 - Few
 - Some (flow)

The following problems cannot be solved for all graphs:
 - Many (can only be solved for DAG's)

When solving for **Many**, we have to test beforehand whether the graph is a DAG or not. If we spot any undirected edges, or any cycles, we have to abort. When parsing the graph, and encountering an undirected edge ( -- ), we could set a boolean in the graph which indicates that the graph is not directed. We could also run an algorithm for spotting cycles, and enable a boolean in the same way (whether its acyclic or not). When we know that the graph is a DAG, we can solve it with something that looks like *longest path*.

For **Some**, we cannot use the approach where we find, for each red vertex, a path to the vertex from the start, and a path to the end from the red vertex. This is because BFS finds the shortes path, and might neglect alternative paths which would enable some path with at least one red. Instead we have to use *min flow*. To do this, we first have to convert the graph to a flow diagram.
