/*
 * Copyright © 2025 Apple Inc. and the Pkl project authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pkl.core.stdlib.base;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.pkl.core.ast.internal.GetClassNode;
import org.pkl.core.ast.internal.GetClassNodeGen;
import org.pkl.core.ast.lambda.ApplyVmFunction1Node;
import org.pkl.core.runtime.VmClass;
import org.pkl.core.runtime.VmFunction;
import org.pkl.core.runtime.VmNull;
import org.pkl.core.runtime.VmObject;
import org.pkl.core.runtime.VmValue;
import org.pkl.core.stdlib.ExternalMethod0Node;
import org.pkl.core.stdlib.ExternalMethod1Node;

public final class AnyNodes {

  private AnyNodes() {}

  public abstract static class getClass extends ExternalMethod0Node {

    @Child private GetClassNode receiverClassNode = GetClassNodeGen.create(getReceiverNode());

    @Specialization
    protected VmClass eval(VirtualFrame frame, Object self) {
      return receiverClassNode.executeWith(frame, self);
    }
  }

  public abstract static class toString extends ExternalMethod0Node {

    @Specialization
    @TruffleBoundary
    protected String evalString(Object self) {
      return self.toString();
    }
  }

  public abstract static class ifNonNull extends ExternalMethod1Node {

    @Child private ApplyVmFunction1Node applyLambdaNode = ApplyVmFunction1Node.create();

    protected static boolean isNonNull(Object obj) {
      return obj.getClass() != VmNull.class;
    }

    @Specialization
    @SuppressWarnings("UnusedParameters")
    protected VmNull eval(VmNull self, VmFunction function) {
      return self;
    }

    @Specialization(guards = "isNonNull(self)")
    protected Object eval(Object self, VmFunction function) {
      return applyLambdaNode.execute(function, self);
    }
  }

  public abstract static class identical extends ExternalMethod1Node {

    @Specialization
    protected boolean eval(Object self, VmValue other) {
      return self == other;
    }
  }
}
