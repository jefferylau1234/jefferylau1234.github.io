# Data Structure Project:
# For each stock, you should store three fields: ID, Price and Volume
# So each stock is an object and have those three fields in its constructor

# Base on the time complexity limitations, here are the reasons about what implements which structure:
# • lookup-by-id(x): O(1) expected time --------> Hash table with a good hash function can do it perfectly: Query cost expected O(1)
# • price-range(p1,p2): O((1 + k)· log n) where k is the number of IDs reported --------> Balanced BST(AVL): Finding elements within a specific range in an AVL tree can be done with in-order traversal in O(k+log n) time, where n is the total number of nodes and k is the number of elements in the range.
# • max-vol(): O(log n) time --------> Max Heap: Peek max root O(1), applying min heap * -1.
# --------------------------------------------------------------------------------
# • insert-new-stock(x,p): O(log n) time --------> insertions of three structures: Hashtable expected O(1) + MaxHeap insertion and then heapify (O(1) + O(log n)) + AVLtree O(log n) = O(log n) total costs
# • update-price(x,p): O(log n) time --------> AVLtree: search + update time complexity O(log n) + O(1) = O(log n) total costs
# • increase-volume(x,v+): O(log n) time --------> Hash table search + update + heapify time complexity O(log n) + O(1) + O(log n) = O(log n) total costs (lazy heap)
# Therefore, I will implement Hash table for the ID, AVLtree for the price and Max Heap for the volume in order to fulfil the above requirements.
# by Jeffery

import heapq
import random
from typing import Dict, List
from numpy.random import random_integers

class Stock:
    def __init__(self, x, p, v):
        self.ID = x
        self.prices = p
        self.volumes = v
    def __lt__(self, other):
        return self.ID < other.ID

class StockTracker:
    def __init__(self):
        self.prices = AVLTree() # BST for price
        self.volumes: List[tuple[int, Stock()]] = []  # Maxheap for vol
        self.stocks: Dict[int, Stock()] = {}  # hash table using python dictionary for ID, and finding its volume and price

    def insert_new_stock(self, x, p):
        if x not in self.stocks: # expected O(1)
            new = Stock(x, p, 0)
            self.stocks.update({x:new}) # expected O(1)
            self.prices.insert(p, x) # O(log n)
            heapq.heappush(self.volumes, (0, new)) # O(log n)

    def update_price(self, x, p):  # expected O(1)+O(log n)+O(log n)= O(log n) in total
        if x in self.stocks: # expected O(1)
            current = self.stocks[x]
            self.prices.delete(current.prices) # O(log n)
            current.prices = p
            self.prices.insert(current.prices, x) # O(log n)

# min-heap * -1 = max-heap
    def increase_volume(self, x, v): # expected O(1)+ O(log n)= O(log n) in total
        if x in self.stocks: # expected O(1)
            current = self.stocks[x]
            current.volumes += v
            heapq.heappush(self.volumes, (-1*current.volumes, current)) # expected O(log n)

# lazy deletion for this heap
    def max_vol(self): # O(log n) in total
        result = []
        current = self.volumes[0]
        while current[0]*-1 != current[1].volumes: # O(log n) for discarding the node if the volume between heap and current stock are different due to lazy deletion heap
            current = heapq.heappop(self.volumes)
        result.append(current[0]*-1) # get the max volume
        result.append(current[1]) # get the stock object which has the max volume
        return result

    def lookup_by_id(self, x): # expected O(1)
        if x in self.stocks:
            current = self.stocks[x]
            print(f"ID :%7d,  price = %5.2f, volume = %3d"%(x, current.prices,current.volumes))

    def price_range(self, low, high): # O(k + log n) in total
        print(f"ID within the range from {low} to {high}: ", end ='\n')
        for i in self.prices.in_range(low, high): #O(k+ log n)
            print(i, end=', ')
        print("\n")



class TreeNode:
    def __init__(self, key, id):
        self.key = key
        self.ID = id
        self.left = None
        self.right = None
        self.height = 1

class AVLTree:
    def __init__(self):
        self.root = None

    def insert(self, key, id):
        self.root = self._insert(self.root, key, id)

    def _insert(self, node, key, id):
        if not node:
            return TreeNode(key, id)
        elif key < node.key:
            node.left = self._insert(node.left, key, id)
        else:
            node.right = self._insert(node.right, key, id)


        node.height = 1 + max(self._get_height(node.left),
                              self._get_height(node.right))


        balance = self._get_balance(node)

        # Left Left Case
        if balance > 1 and key < node.left.key:
            return self._right_rotate(node)
        # Right Right Case
        if balance < -1 and key > node.right.key:
            return self._left_rotate(node)
        # Left Right Case
        if balance > 1 and key > node.left.key:
            node.left = self._left_rotate(node.left)
            return self._right_rotate(node)
        # Right Left Case
        if balance < -1 and key < node.right.key:
            node.right = self._right_rotate(node.right)
            return self._left_rotate(node)
        return node

    def delete(self, key):
        self.root = self._delete(self.root, key)
    def _delete(self, node, key):
        if not node:
            return node

        if key < node.key:
            node.left = self._delete(node.left, key)
        elif key > node.key:
            node.right = self._delete(node.right, key)
        else:
            if node.left is None:
                return node.right
            elif node.right is None:
                return node.left

            temp = self._min_value_node(node.right)
            node.key = temp.key
            node.right = self._delete(node.right, temp.key)

        if node is None:
            return node

        node.height = 1 + max(self._get_height(node.left),
                              self._get_height(node.right))

        balance = self._get_balance(node)


        # Left Left
        if balance > 1 and self._get_balance(node.left) >= 0:
            return self._right_rotate(node)

        # Left Right
        if balance > 1 and self._get_balance(node.left) < 0:
            node.left = self._left_rotate(node.left)
            return self._right_rotate(node)

        # Right Right
        if balance < -1 and self._get_balance(node.right) <= 0:
            return self._left_rotate(node)

        # Right Left
        if balance < -1 and self._get_balance(node.right) > 0:
            node.right = self._right_rotate(node.right)
            return self._left_rotate(node)

        return node

    def _left_rotate(self, z):
        if z is None or z.right is None:
            return z

        y = z.right
        T2 = y.left

        y.left = z
        z.right = T2

        z.height = 1 + max(self._get_height(z.left), self._get_height(z.right))
        y.height = 1 + max(self._get_height(y.left), self._get_height(y.right))

        return y

    def _right_rotate(self, z):
        if z is None or z.left is None:
            return z

        y = z.left
        T3 = y.right

        y.right = z
        z.left = T3

        z.height = 1 + max(self._get_height(z.left), self._get_height(z.right))
        y.height = 1 + max(self._get_height(y.left), self._get_height(y.right))

        return y
    def _get_height(self, node):
        if not node:
            return 0
        return node.height
    def _get_balance(self, node):
        if not node:
            return 0
        return self._get_height(node.left) - self._get_height(node.right)
    def _min_value_node(self, node):
        current = node
        while current.left is not None:
            current = current.left
        return current

    # O(log n + k)
    def in_range(self, low, high):
        result = []
        self._in_range(low, high, self.root, result)
        return result
    def _in_range(self, low, high, node, result):
        if node is None:
            return None
        if node.key > low: # go to left child
            self._in_range(low, high, node.left, result)
        if node.key < high: # go to right child
            self._in_range(low, high, node.right, result)
        if node.key >= low and node.key <= high: # append it
            result.append(node.ID)



def main():
    a = StockTracker()
    j= 0
    k = 0
    for i in range(10000): # testing operations from the project description
        ID = random_integers(1,1000000)
        a.insert_new_stock(ID, 0)

        price = round(random.uniform(1, 100), 2)
        a.update_price(ID, price)
        j+=1

        volume = random_integers(1,100)
        a.increase_volume(ID, volume)
        k+=1

        a.lookup_by_id(ID)

        if j %1000 == 0:
            a.price_range(price, price+2)

        if k %100 == 0:
            print("MaxVol:\nID :%7d,  price = %5.2f, volume = %3d\n"%(a.max_vol()[1].ID, a.max_vol()[1].prices, a.max_vol()[1].volumes))


if __name__ == '__main__':
    main()
