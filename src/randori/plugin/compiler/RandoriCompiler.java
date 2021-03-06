/***
 * Copyright 2013 Teoti Graphix, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * @author Michael Schmalle <mschmalle@teotigraphix.com>
 */

package randori.plugin.compiler;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Chunk;
import org.jetbrains.annotations.NotNull;
import randori.plugin.components.RandoriProjectComponent;
import randori.plugin.util.VFileUtils;

import java.util.List;

/**
 * IDEA Compiler class for calling the internal compiler API.
 * 
 * @author Frédéric THOMAS
 */
public class RandoriCompiler implements TranslatingCompiler
{

    private static final Logger LOG = Logger.getInstance("#randori.compiler.RandoriCompiler");
    protected final Project project;
    private final RandoriProjectComponent projectComponent;

    public RandoriCompiler(Project project)
    {
        this.project = project;

        projectComponent = project.getComponent(RandoriProjectComponent.class);
    }

    @Override
    public boolean isCompilableFile(VirtualFile file, CompileContext context)
    {
        return VFileUtils.extensionEquals(file.getPath(), "as");
    }

    @Override
    public void compile(CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink sink)
    {
        context.getProgressIndicator().checkCanceled();
        context.getProgressIndicator().setText("Starting Randori compiler...");

        List<VirtualFile> modifiedFiles = projectComponent.getModifiedFiles();

        if (context.isMake() && modifiedFiles.size() > 0)
        {
            projectComponent.build(false, true);
        }
        else
        {
            projectComponent.build(true, true);
        }

        modifiedFiles.removeAll(modifiedFiles);
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "Randori Compiler";
    }

    @Override
    public boolean validateConfiguration(CompileScope scope)
    {
        return projectComponent.validateConfiguration(scope);
    }
}
