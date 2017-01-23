# MultiChoiceMode-RecyclerView

[![API](https://img.shields.io/badge/API-12%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=12) <br>
This repository provides an API for creating a multiple choice mode in `RecyclerView`.

## A brief glance at the sample ##
![](https://github.com/vpaliyX/MultiChoiceMode-RecyclerView/blob/master/art/ezgif.com-video-to-gif%20(5).gif)


## How does it work? ##

The API contains three main classes:`BaseAdapter`, `MultiMode` and `StateTracker`. The last one has a package-private access, so you don't need to deal with it at all.

Basically, you need to do the following steps in order to make it work:
  
 * Get a toolbar and create a `MultiMode` instance using the builder approach:
    ```java
      /** You can set colors, menu to inflate and callback to use, navigation icon, logo, title and subtitle */
      MultiMode mode=new MultiMode.Builder(actionBar,this)
                .setMenu(R.menu.list_menu,new MultiMode.Callback() {
                    @Override
                    public boolean onMenuItemClick(BaseAdapter adapter, MenuItem item) {
                        // you can use any function of the adapter in order to work with selected items
                        return false;
                    }
                })
                .setStatusBarColor(Color.MAGENTA)
                .setBackgroundColor(Color.WHITE)
                .setNavigationIcon(getResources().getDrawable(R.drawable.ic_clear_black_24dp))
                .build();
    ```
 
 * Extend the `BaseAdapter` class and initialize it using the `MultiMode` instance:
 
    ```java
     public class Adapter extends BaseAdapter {
     
         public Adapter(MultiMode mode, boolean animate){
            super(mode,animate);
         }
         
         public Adapter(MultiMode mode, boolean animate, @NonNull Bundle savedInstanceState){
            super(mode,animate,savedInstanceState);
         }
         
         //implement the rest of the important methods which RecyclerView has
         
         @Override
         public void removeAt(int index){
            //write your implementation of this method
         }
     }
    ```
 * Then create a `ViewHolder` class of your adapter using a special class in `BaseAdapter` and implement the next methods:
  
    ```java
    public class ViewHolder extends BaseAdapter.BaseViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            //bind your views here
        }

        @Override
        public void onBindData() {
            //install your views with the data here 
            
            //after you have installed your data, call this method, otherwise you will not get any animation
            determineState();
        }

        @Override
        public void updateBackground() {
            if(isChecked(getAdapterPosition())){
                //if this item has been selected, set an appropriate background
            }else{
               //install the background of your view
            }
        }

        @Override
        public void enterState() {
            super.enterState();
            //animate your view 
        }

        @Override
        public void animatedState() {
            //set your view to the animated state, without animation 
        }

        @Override
        public void exitState() {
            super.exitState();
            //create your exit animation
        }

        @Override
        public void defaultState() {
           //set your view to the default state
        }
     }
    ```
  
 ### What if screen rotation occurs? ###
  
  As long as you have a reference to your adapter, screen rotation is not a big deal. You can easily save and restore the state of the adapter using the following code:
 
 * Save the state:
   ```java
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveState(outState);
    }    
    ```
 * Restore:
    ```java   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //do some stuff$
        
        if(savedInstanceState!=null){
          adapter=new Adapter(mode,true,savedInstanceState);
        }else{
          adapter=new Adapter(mode,true);
        }
    }
    ```
  
 Also you need to keep in mind that activity can be stopped by launching another app or receiving a phone call.
 In this particular case that is not a problem as well, as long as you keep the reference to your adapter, you can restore it:
    ```java   
    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.onResume();
        }
    }
   ```
 ### Understanding of animation in the `BaseAdapter.BaseViewHolder` class ###
 
 If you decide to animate an item upon click, you have to understand how it works. Basically, there are 4 states of the item:<br>
 
  1) `Enter` state<br>
     This state represents an animation that has to occur when an item is clicked.
     For example, you can use this implementation of the method:
     
  ```java   
       @Override
       public void enterState() {
          super.enterState();
          itemView.animate()
            .scaleX(0.85f)
            .scaleY(0.85f)
            .setDuration(180).start();
       }
  ```
  
  2) `Animated` state<br>
    This is a state of an item that has been animated, and doesn't need this animation again. So, the main purpose of this method is to put a view into the animated state.<br>
   ```java   
        @Override
        public void animatedState() {
          itemView.setScale(0.85f);
          itemView.setScale(0.85f);
        }
   ```
   
  3) `Exit` state<br>
     This state is responsible for animating a view in the normal state, some kind of back animation. Here is the example:<br>
   ```java   
        @Override
        public void exitState() {
          super.exitState();
          itemView.animate()
            .scaleX(1.f)
            .scaleY(1.f)
            .setDuration(180).start();
        }
   ```
   
  3) `Default` state<br>
      That is a default or normal state of your item. The following method can be a representation of such state:
      
  ```java   
        @Override
        public void defaultState() {
           if(itemView.getScale()<1.f){
              itemView.setScale(1.f);
              itemView.setScale(1.f);
            }
        }
   ```
      
 

  
  
      
      
