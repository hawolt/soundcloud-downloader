package com.hawolt.data.media.search.endpoint;

import com.hawolt.data.media.search.endpoint.impl.ClientInstruction;
import com.hawolt.data.media.search.endpoint.impl.SubstituteInstruction;
import com.hawolt.data.media.search.endpoint.impl.TimestampInstruction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created: 15/11/2022 17:19
 * Author: Twitter @hawolt
 **/

public class InstructionInterpreter {
    private static final Map<String, Instruction> INSTRUCTION_MAP = new HashMap<>();

    static {
        INSTRUCTION_MAP.put("substitute", new SubstituteInstruction());
        INSTRUCTION_MAP.put("timestamp", new TimestampInstruction());
        INSTRUCTION_MAP.put("client", new ClientInstruction());
    }

    public static String parse(String in) throws Exception {
        int[] occurrences = new int[0];
        int index = -1;
        while ((index = in.indexOf('$', index + 1)) != -1) {
            occurrences = Arrays.copyOf(occurrences, occurrences.length + 1);
            occurrences[occurrences.length - 1] = index;
        }
        StringBuilder builder = new StringBuilder(in);
        for (int i = occurrences.length - 1; i >= 0; i--) {
            int start = builder.indexOf("(", occurrences[i] + 1);
            if (start == -1) continue;
            int end = builder.indexOf(")", start);
            String command = builder.substring(start + 1, end);
            String[] args = command.split(" ");
            if (INSTRUCTION_MAP.containsKey(args[0])) {
                Instruction instruction = INSTRUCTION_MAP.get(args[0]);
                String result = instruction.manipulate(in, args);
                builder.replace(occurrences[i], end + 1, result);
            }
        }
        return builder.toString().trim();
    }
}
