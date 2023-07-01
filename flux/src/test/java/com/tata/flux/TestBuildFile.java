package com.tata.flux;
import com.tata.flux.service.FileWriterUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ExtendWith(MockitoExtension.class)
public class TestBuildFile {
    @Test
    void test(){
        int amount = 9_000_000;
        FileWriterUtility fileWriterUtility = new FileWriterUtility();
        fileWriterUtility.build(getData(amount),"header","fileName",".dat").subscribe();
    }
    private Flux<String> getData(int n) {
       return Flux.range(1,n)
               .map(i -> "202305012300".toString().repeat(10));
    }

    @Test
    void test2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        FileWriterUtility fileWriterUtility = new FileWriterUtility();
        Method method = FileWriterUtility.class.getDeclaredMethod("createFile",String.class, String.class, String.class);
        method.setAccessible(true);
        //method.invoke(fileWriterUtility,"header","fileName_",".dat");
        method.invoke(fileWriterUtility,"","fileName_",".dat");
    }
}
