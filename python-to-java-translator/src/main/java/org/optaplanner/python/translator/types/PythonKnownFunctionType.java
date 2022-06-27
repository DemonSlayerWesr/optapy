package org.optaplanner.python.translator.types;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.optaplanner.python.translator.MethodDescriptor;
import org.optaplanner.python.translator.PythonFunctionSignature;

public class PythonKnownFunctionType extends PythonLikeType {
    final List<PythonFunctionSignature> overloadFunctionSignatureList;

    public PythonKnownFunctionType(String methodName, List<PythonFunctionSignature> overloadFunctionSignatureList) {
        super("function-" + methodName, PythonKnownFunctionType.class, List.of(PythonLikeFunction.getFunctionType()));
        this.overloadFunctionSignatureList = overloadFunctionSignatureList;
    }

    public List<PythonFunctionSignature> getOverloadFunctionSignatureList() {
        return overloadFunctionSignatureList;
    }

    public boolean isStatic() {
        return overloadFunctionSignatureList.get(0).getMethodDescriptor().getMethodType() == MethodDescriptor.MethodType.STATIC;
    }

    public Optional<PythonFunctionSignature> getDefaultFunctionSignature() {
        return overloadFunctionSignatureList.stream().findAny();
    }

    public Optional<PythonFunctionSignature> getFunctionForParameters(PythonLikeType... parameters) {
        List<PythonFunctionSignature> matchingOverloads = overloadFunctionSignatureList.stream()
                .filter(signature -> signature.matchesParameters(parameters))
                .collect(Collectors.toList());

        if (matchingOverloads.isEmpty()) {
            return Optional.empty();
        }

        PythonFunctionSignature best = matchingOverloads.get(0);
        for (PythonFunctionSignature signature : matchingOverloads) {
            if (signature.moreSpecificThan(best)) {
                best = signature;
            }
        }
        return Optional.of(best);
    }
}
