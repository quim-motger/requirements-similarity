package com.quim.tfm.similarity;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.service.SimilarityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NLPreprocessingTest {

    @Autowired
    private SimilarityService similarityService;

    @Test
    void NLPreprocessPipeline() {

        Requirement r1 = new Requirement(
                "QTCREATORBUG-18204",
                "GDB debugger wrapper does not correctly display pointers as arrays.",
                "This is an unfixed bug from 5.2 - that report was never evaluated:\\r\\n\\r\\nhttps://bugreports.qt.io/browse/QTCREATORBUG-17803\\r\\n\\r\\nQTCreator's wrapper around GDB does not dereference pointers when it is told to view the pointer with \"Array of size _______\". Instead it shows memory starting at the address of the pointer variable itself.\\r\\n\\r\\nTake a pointer and try to Change the Local Display Format to show it as an Array of 10 items. Instead of dereferencing the pointer and displaying 10 consecutive items at that memory address, the debugger tries to who you 10 items starting at the address of the pointer variable on the stack.\\r\\n\\r\\nThis image shows examining a pointer on the stack:\\r\\n!http://faculty.chemeketa.edu/ascholer/temp/Capture1.PNG!\\r\\n\\r\\nNote that the pointer holds the address 0xc912a0 and is located at 0x0029feac. Then I try do display as Array of 10 items and get:\\r\\n!http://faculty.chemeketa.edu/ascholer/temp/Capture2.PNG!\\r\\n\\r\\nThe base address of it is displaying is 0x0029feac and the first value it is displaying is the address 0xc912a0 displayed as a decimal 13177504.");

        Requirement r2 = new Requirement(
                "QTCREATORBUG-17803",
                "Debugger shows wrong address for pointer treated as array",
                "Take a pointer and try to Change the Local Display Format to show it as an Array of 10 items. Instead of dereferencing the pointer and displaying 10 consecutive items at that memory address, the debugger tries to who you 10 items starting at the address of the pointer variable on the stack. \\r\\n\\r\\nThis image shows examining a pointer on the stack:\\r\\n!http://faculty.chemeketa.edu/ascholer/temp/Capture1.PNG!\\r\\n\\r\\nNote that the pointer holds the address 0xc912a0 and is located at 0x0029feac. Then I try do display as Array of 10 items and get:\\r\\n!http://faculty.chemeketa.edu/ascholer/temp/Capture2.PNG!\\r\\n\\r\\nThe base address of it is displaying is 0x0029feac and the first value it is displaying is the address 0xc912a0 displayed as a decimal 13177504.\\r\\n\\r\\n\\r\\n");

        //similarityService.bm25f_reqPairWithPreprocess(r1, r2);

    }

}
