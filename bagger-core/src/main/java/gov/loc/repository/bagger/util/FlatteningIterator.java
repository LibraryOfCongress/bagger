package gov.loc.repository.bagger.util;

   import java.lang.reflect.Array;
   import java.util.Arrays;
   import java.util.Iterator;
   import java.util.NoSuchElementException;
   import java.util.Stack;

   /**
    * An iterator that 'flattens out' collections, iterators, arrays, etc.
    *
    * That is it will iterate out their contents in order, descending into any
    * iterators, iterables or arrays provided to it.
    *
    * An example (not valid Java for brevity - some type declarations are ommitted):
    *
    * new FlattingIterator({1, 2, 3}, {{1, 2}, {3}}, new ArrayList({1, 2, 3}))
    *
    * Will iterate through the sequence 1, 2, 3, 1, 2, 3, 1, 2, 3.
    *
    * Note that this implements a non-generic version of the Iterator interface so
    * may be cast appropriately - it's very hard to give this class an appropriate
    * generic type.
    *
    * @author david
   * @param <T>
    */
   public class FlatteningIterator implements Iterator<Object>
   {
       // Marker object. This is never exposed outside this class, so can be guaranteed
       // to be != anything else. We use it to indicate an absense of any other object.
       private final Object blank = new Object();

       /* This stack stores all the iterators found so far. The head of the stack is
        * the iterator which we are currently progressing through */
       private final Stack<Iterator<?>> iterators = new Stack<Iterator<?>>();

       // Storage field for the next element to be returned. blank when the next element
       // is currently unknown.
       private Object next = blank;

       public FlatteningIterator(Object... objects){
           this.iterators.push(Arrays.asList(objects).iterator());}

       @Override
      public void remove(){
           /* Not implemented */}

       private void moveToNext(){
           if ((next == blank) && !this.iterators.empty() ) {
               if (!iterators.peek().hasNext()){
                   iterators.pop();
                   moveToNext();}
                   else{
                       final Object nextInInteration = iterators.peek().next();
                       if (nextInInteration instanceof Iterator){
                         iterators.push((Iterator<?>)nextInInteration);
                         moveToNext();}
                       else if (nextInInteration instanceof Iterable){
                          iterators.push(((Iterable<?>)nextInInteration).iterator());
                          moveToNext();}
                       else if (nextInInteration instanceof Array){
                           iterators.push(Arrays.asList((Array)nextInInteration).iterator());
                           moveToNext();}
                       else this.next = nextInInteration;}}}

       /**
        * Returns the next element in our iteration, throwing a NoSuchElementException
        * if none is found.
        */
       @Override
      public Object next() throws NoSuchElementException {
           moveToNext();

           if (this.next == blank) throw new NoSuchElementException();
          Object nextCopy = this.next;
           this.next = blank;
           return nextCopy;}

       /**
        * Returns if there are any objects left to iterate over. This method
        * can change the internal state of the object when it is called, but repeated
        * calls to it will not have any additional side effects.
        */
       @Override
      public boolean hasNext(){
           moveToNext();
           return (this.next != blank);}
   }