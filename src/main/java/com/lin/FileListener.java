package com.lin;

import java.io.File;

/**
 * 文件监听器，当对应的文件该变时，应该触发此监听器对应的方法
 *
 * @author jianglinzou
 * @date 2019/3/21 上午10:47
 */
public interface FileListener extends ShouldAccept {


    /**
     * 当对应的文件改变时，将会触发此方法
     *
     * @param file
     */
    public void onFileChange(File file);


    public void onFileCreate(File file);


    public void onFileDelete(File file);


    /**
     * 返回该fileListener的名字，当给fileListener执行失败时，将会
     * 调用此方法已做记录，因此，建议对于不同的fileListener的name方法
     * 此方法应该返回不同的值
     *
     * @return
     */
    public String name();


    /**
     * 监听器的执行顺序，返回的值越大，越先执行
     *
     * @return
     */
    public int getOrder();


}
