# Dragger

Library: DraggerPageLayout

可拖动View到任意位置的Layout（可分页）

展示效果：

![image](https://github.com/huare/Dragger/blob/master/Dragger/screens/dragger.gif)


V1.0.0

DraggerPageLayout功能：
1，缓存所有被用于拖拽到屏幕实例化的View
2，缓存所有拖拽后View的layout（用于在屏幕定位的数据）
3，根据每页缓存的layouts刷新view（用不到的view回归原地等待操作，缓存view不够创建新View）



升级考虑：
1，配置Adapter数据源：配置分页数据
2，同（RecycleView.ViewHolder)的DraggerPageLayout.ViewHolder刷新数据


使用：
1，DraggerPageLayout作为Layout布局，里面必须包含一个id为：tv_sign的View（该View的位置就是实例化拖拽View的始发处）
2，添加View（根据tv_sign的位置，实例化一个拖拽的View覆盖上面）
     public void addClick(View v) {

        TextView textView = new TextView(this);
        textView.setText("王华" + count++);
        textView.setBackgroundColor(Color.CYAN);
     
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tv_sign.getWidth(), tv_sign.getHeight());
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.leftMargin = tv_sign.getLeft();
        textView.setLayoutParams(layoutParams);

        layout.addDragView(textView);

    }
    
3，新增View随意拖拽至屏幕任意位置

4，模拟分页
     //上一页
     public void lastClick(View v) {
        if (page == 0) {
            Toast.makeText(this, "没有上一页了", Toast.LENGTH_SHORT).show();
        } else {
            layout.selectCurrentPage(--page);
        }
    }
    
    //下一页
    public void nextClick(View v) {
        layout.selectCurrentPage(++page);
    }
