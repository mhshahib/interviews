package interviews.trees;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Trie.
 * @author Francois Rousseau
 */
public class Trie {
  private Node root = new Node();

  /**
   * Insert a given string inside the trie.
   */
  public void add(String s) {
    if(s == null) return;
    root = add(root, s.toCharArray(), 0);
  }

  /**
   * Delete every element inside the trie.
   */
  public void clear() {
    root.clear();
  }

  /**
   * Return the most frequent suffix to append to the given string.
   * Returns null if none exists or if the given string is already a valid prefix.
   */
  public String completion(String s) {
    return completion(root, s, false);
  }

  /**
   * Return the most frequent suffix to append to the given string.
   * Returns null if none exists.
   */
  public String completionForced(String s) {
    return completion(root, s, true);
  }

  /**
   * Check if the trie contains a given string.
   * Return true even if the string is not a valid word.
   */
  public boolean contains(String s) {
    return contains(s, false);
  }

  /**
   * Check if a given string is a valid word.
   * By valid, we mean once inserted as a string and not only as a substring in the trie.
   */
  public boolean isValid(String s) {
    return contains(s, true);
  }

  /**
   * Return the frequency of a given string in the trie.
   * Valid or not.
   */
  public int frequency(String s) {
    if(s == null) return 0;
    return frequency(root, s.toCharArray(), 0);
  }

  /**
   * Return the longest valid prefix in the trie for a given string.
   */
  public String longestPrefix(String s) {
    if(s == null) return null;
    int length = longestPrefix(root, s.toCharArray(), 0, 0);
    return s.substring(0, length);
  }

  /**
   * Remove the given string from the trie.
   * @return boolean indicating if the operation was done or not.
   */
  public void remove(String s) {
    if(s == null) return;
    root = remove(root, s.toCharArray(), 0);
  }

  /**
   * BFS traversal of the trie.
   * @return String
   */
  public String toString() {
    Queue<Node> queue = new ArrayDeque<Node>();
    StringBuffer buffer = new StringBuffer();

    queue.add(root);
    while(!queue.isEmpty()) {
      Node node = queue.poll();
      for(Character c: node.keys()) {
        buffer.append(c);
        queue.add(node.get(c));
      }
    }
    return buffer.toString();
  }

  /**
   * Return all the valid words in the Trie.
   */
  public Iterable<String> words() {
    Queue<String> queue = new ArrayDeque<String>();
    collect(root, new StringBuffer(), queue);
    return queue;
  }

  /**
   * Return all the valid words starting with prefix in the Trie.
   */
  public Iterable<String> wordsWithPrefix(String prefix) {
    Queue<String> queue = new ArrayDeque<String>();
    Node node = get(root, prefix.toCharArray(), 0);
    collect(node, new StringBuffer(prefix), queue);
    return queue;
  }


  /**
   * Create child node for the ith character of the array.
   */
  private Node add(Node node, char[] arr, int i) {
    if(node == null) {
      node = new Node();
    }
    if(i == arr.length) {
      node.isValid = true;
      return node;
    }
    char c = arr[i];
    node.put(c, add(node.get(c), arr, i + 1));
    node.updateFrequency(c);
    return node;
  }

  /**
   * Collect recursively all the valid words starting with prefix in the Trie.
   */
  private void collect(Node node, StringBuffer prefix, Queue<String> q) {
   if(node == null) return;
   if(node.isValid) q.add(prefix.toString());
   for(Character c: node.keys()) {
     prefix.append(c);
     collect(node.get(c), prefix, q);
     prefix.deleteCharAt(prefix.length() - 1);
   }
  }

  /**
   * Return the most frequent suffix to append to the given string.
   * @param force a boolean indicating if we want to auto-complete
   * even if the string is already a valid prefix.
   * @return the auto-completed suffix, null if there is none
   */
  private String completion(Node node, String s, boolean force) {
    for(int i = 0; i < s.length(); i++) {  // loop until you get the last child
      Node child = node.get(s.charAt(i));
      if(child == null) {  // the string we want to complete is not even in the trie
        return null;
      }
      node = child;
    }
    if((node.isValid && !force) || node.mostFrequent == '\u0000') {
      return null;
    }
    StringBuffer buffer = new StringBuffer();
    buffer.append(node.mostFrequent);
    completion(node.get(node.mostFrequent), buffer);
    return buffer.toString();
  }
  
  /**
   * Fill a StringBuffer with the most frequent suffix. Ends with a valid one is reached.
   */
  private void completion(Node node, StringBuffer buffer) {
    if(node.isValid) return;
    buffer.append(node.mostFrequent);
    completion(node.get(node.mostFrequent), buffer);
  }

  /**
   * Check whether a given string is in the trie.
   * Valid or not, depending on the argument isValid.
   */
  private boolean contains(String s, boolean isValid) {
    if(s == null) return false;
    return contains(root, s.toCharArray(), 0, isValid);
  }

  /**
   * Check whether a given array of characters is in the trie.
   * Valid or not, depending on the argument isValid.
   */
  private boolean contains(Node node, char[] arr, int i, boolean isValid) {
    if(node == null)    return false;
    if(i == arr.length) return isValid ? node.isValid : true;
    return contains(node.get(arr[i]), arr, i + 1, isValid);
  }

  /**
   * Return the frequency of the leaf corresponding to the given array.
   * Recursion on the index i until the end of the array.
   * @return frequency
   */
  private int frequency(Node node, char[] arr, int i) {
    if(node == null)    return 0;
    if(i == arr.length) return node.frequency;
    return frequency(node.get(arr[i]), arr, i+1);
  }

  /**
   * Return the node corresponding to the array of characters.
   * Null if none exists.
   */
  private Node get(Node node, char[] arr, int i) {
    if(node == null)    return null;
    if(i == arr.length) return node;
    return get(node.get(arr[i]), arr, i + 1);
  }

  /**
   * Return the length of the longest valid word prefixing the given array of characters.
   */
  private int longestPrefix(Node node, char[] arr, int i, int length) {
    if(node == null)    return length;  // return the length of the longest valid word seen so far
    if(node.isValid)    length = i;     // update the length of the longest valid word seen so far
    if(i == arr.length) return length;  // return the length of the longest valid word seen so far
    return longestPrefix(node.get(arr[i]), arr, i + 1, length);
  }

  /**
   * Remove the given array of characters from the trie.
   * @return Pair<Boolean, TrieNode> the boolean indicating if it was remove and the TrieNode
   * the node to remove because there is no child.
   */
  private Node remove(Node node, char[] arr, int i) {
    if(i == arr.length) {  // we reached the end of the array
      if(node.isValid) {  // if it is a valid word, we remove its validity
        node.isValid = false;
        if(node.isEmpty()) {  // if there is no child, we tell the parent to remove the node
          return null;
        } else {  // otherwise we don't
          return node;
        }
      }
      return node;  // the node is not valid so we don't delete it
    }
    char c = arr[i];
    Node child = node.get(c);
    if(child == null) {  // word is not in the trie, nothing to remove
      return node;
    }
    child = remove(child, arr, i + 1);
    if(child == null) {  // the child is to be deleted
      node.remove(c);
    }
    // if there is no more child and the node is not valid, we tell the parent to remove the node
    if(!node.isValid && node.isEmpty()) {
      return null;
    }
    return node;
  }


  /**
   * Private inner class for an internal Trie node.
   */
  private static class Node {
    private int frequency;
    private boolean isValid;
    private Map<Character, Node> children = new HashMap<Character, Node>(10);
    private char mostFrequent;

    private void clear() {
      children.clear();
    }

    private Node get(char key) {
      return children.get(key);
    }

    private boolean isEmpty() {
      return children.isEmpty();
    }
 
    private Collection<Character> keys() {
      return children.keySet();
    }

    private void put(char key, Node node) {
      children.put(key, node);
    }

    private void remove(char key) {
      children.remove(key);
    }

    private void updateFrequency(char c) {
      Node child = get(c);
      child.frequency++;
      if(mostFrequent == '\u0000' || get(mostFrequent).frequency < child.frequency) {
        mostFrequent = c;
      }
    }
  }
}