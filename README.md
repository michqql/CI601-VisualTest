<h1>Steps to get project to run:</h1>
<h3>In IntelliJ IDEA:</h3>
<ol>
    <li>In the toolbar, click 'File' (Top Left)</li>
    <li>Click 'Project Structure'</li>
    <li>
        Under 'Project Settings' (Left Side), click 'Project'
        <ol>
            <li>Select SDK as version 24 or greater</li>
            <li>Select language level as 22 or greater</li>
        </ol>
    </li>
    <li>
        Under 'Project Settings' (Left Side), click 'Modules'
        <ol>
            <li>From tab options: 'Sources, Paths, Dependencies', select 'Dependencies'</li>
            <li>From 'https://gluonhq.com/products/javafx/' download JavaFX 25 (Type: SDK) (Downloads a ZIP)</li>
            <li>Unzip the file</li>
            <li>Back in 'Dependencies' (See step 4.i) click '+' button</li>
            <li>Navigate to the location you saved JavaFX and select the 'lib' folder</li>
            <li>Ensure 'Scope' is set to 'Compile'</li>
        </ol>
    </li>

</ol>

