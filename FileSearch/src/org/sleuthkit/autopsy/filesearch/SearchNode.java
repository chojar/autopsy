/*
 * Autopsy Forensic Browser
 *
 * Copyright 2011 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sleuthkit.autopsy.filesearch;

import java.util.ArrayList;
import org.openide.nodes.AbstractNode;
import org.sleuthkit.autopsy.datamodel.ContentNode;
import org.sleuthkit.autopsy.datamodel.ContentNodeVisitor;
import org.sleuthkit.datamodel.FsContent;

/**
 *
 * @author jantonius
 */
class SearchNode extends AbstractNode implements ContentNode {

    private SearchChildren children;

    SearchNode(ArrayList<FsContent> keys) {
        super(new SearchChildren(true, keys));
        this.children = (SearchChildren)this.getChildren();
    }

    @Override
    public String getName() {
        return "Search Result";
    }
    

    @Override
    public <T> T accept(ContentNodeVisitor<T> v) {
        //TODO: figure out how to deal with visitors
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
