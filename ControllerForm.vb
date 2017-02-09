Imports System.IO
Imports System.Text


Public Class frmController
    Inherits System.Windows.Forms.Form

    Public Shared OsShell As String
    Public Shared DPPEJDirName As String
    Public Shared JavaDirName As String
    Public Shared UserDirName As String
    Public Shared ControllerDirName As String
    Public Shared ServerAddress As String
    Public Shared DocsDirName As String
    Public Shared ProjectDirName As String
    Public Shared noClients As Integer

    Dim frmSplash As New splash()
    Dim frmExecute As New ExecuteForm()
    Dim frmCompile As New CompileForm()
    Dim frmOptions As New options()
    Dim frmsettings As New Settings()
    Dim frmChangeDir As New ChangeDir()


    ReadOnly Property DPPEJDir() As String
        Get
            DPPEJDir = DPPEJDirName
        End Get
    End Property
    ReadOnly Property JavaDir() As String
        Get
            JavaDir = JavaDirName
        End Get
    End Property
    Property UserDir() As String
        Get
            UserDir = UserDirName
        End Get
        Set(ByVal Value As String)
            If (Directory.Exists(Value)) Then
                UserDir = Value
            Else
                MsgBox("Attempted to set an invalid directory", MsgBoxStyle.Critical, "Error")
            End If
        End Set
    End Property
    ReadOnly Property ControllerDir() As String
        Get
            ControllerDir = ControllerDirName
        End Get
    End Property

    'FILE that contains info for the Front End
    'Vital!

    Dim IniFileName As New String("Controller.ini")

    Private Sub cmdStartReg_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdStartReg.Click
        Shell(DPPEJDirName + "\bin\" + "start_daemonregistry.bat", AppWinStyle.NormalFocus)
    End Sub



    Private Sub frmController_Load(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MyBase.Load
        'File must be created before the Controller can be started
        'FILE SYNTAX
        'DPPEJDirName
        'JavaDirName
        'UserDirName
        'ControllerDirName
        'DocsDirName
        'ProjectDirName
        'EOF
        ' be sure to save it in ControllerDir\bin
        'frmSplash.Visible = True
        'Me.Visible = False

        Dim IniFileFS As New FileStream(IniFileName, FileMode.Open)
        Dim IniFileSR As New StreamReader(IniFileFS)
        DPPEJDirName = IniFileSR.ReadLine()
        JavaDirName = IniFileSR.ReadLine()
        UserDirName = IniFileSR.ReadLine()
        ControllerDirName = IniFileSR.ReadLine()
        DocsDirName = IniFileSR.ReadLine()
        ProjectDirName = IniFileSR.ReadLine()

        IniFileSR.Close()
        OsShell = "explorer " + UserDirName
        noClients = 1
        cboNoClients.SelectedValue = 1
        ServerAddress = ReadServerAddressFile()


    End Sub

    Private Function ReadServerAddressFile() As String
        Directory.SetCurrentDirectory(DPPEJDir + "/bin")
        Dim FS As New FileStream("daemonregistry.properties", FileMode.Open)
        Dim SR As New StreamReader(FS)
        Dim fileLine As StringBuilder
        fileLine = New StringBuilder(SR.ReadLine())
        Dim serverAddr As StringBuilder
        serverAddr = fileLine.Replace("daemonregistry=", "")
        SR.Close()
        Directory.SetCurrentDirectory(UserDirName)
        Return serverAddr.ToString
    End Function

    Private Sub notify1_MouseDown(ByVal sender As System.Object, ByVal e As System.Windows.Forms.MouseEventArgs) Handles systrayicon.MouseDown

    End Sub

    Private Sub cmdExecute_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdExecute.Click
        frmExecute.Visible = True
    End Sub


#Region " Windows Form Designer generated code "

#End Region

    Protected Overrides Sub Finalize()
        MyBase.Finalize()
        Directory.SetCurrentDirectory(ControllerDirName + "\bin")
        Dim IniFileFS As New FileStream(IniFileName, FileMode.Open)
        Dim IniFileSW As New StreamWriter(IniFileFS)


        'VERY IMPORTANT
        'DO NOT CHANGE THE ORDER
        'OF WRITING THE DIRECTORIES

        IniFileSW.Write(DPPEJDirName)
        IniFileSW.WriteLine(JavaDirName)
        IniFileSW.WriteLine(UserDirName)
        IniFileSW.WriteLine(ControllerDirName)
        IniFileSW.WriteLine(DocsDirName)
        IniFileSW.WriteLine(ProjectDirName)
        IniFileSW.Close()

    End Sub


    Private Sub cboNoClients_SelectedIndexChanged(ByVal sender As System.Object, ByVal e As System.EventArgs)
        noClients = cboNoClients.SelectedValue
    End Sub

    Private Sub cmdStopReg_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdStopReg.Click
        Shell(DPPEJDirName + "\bin\" + "stop_daemonregistry.bat", AppWinStyle.NormalFocus)
    End Sub

    Private Sub cmdStartD_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdStartD.Click
        Shell(DPPEJDirName + "\bin\" + "start_daemon.bat", AppWinStyle.NormalFocus)
    End Sub

    Private Sub cmdStartBgD_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdStartBgD.Click
        Shell(DPPEJDirName + "\bin\" + "start_daemon_background.bat", AppWinStyle.NormalFocus)
    End Sub

    Private Sub cmdStopAllDs_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdStopAllDs.Click
        Shell(DPPEJDirName + "\bin\" + "stop_all_daemons.bat", AppWinStyle.NormalFocus)
    End Sub

    Private Sub cmdStopLocDs_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdStopLocDs.Click
        Shell(DPPEJDirName + "\bin\" + "stop_local_daemons.bat", AppWinStyle.NormalFocus)
    End Sub

    Private Sub cmdCompile_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdCompile.Click
        frmCompile.Visible = True
    End Sub

    Private Sub cmdDirectory_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdDirectory.Click
        frmChangeDir.Visible = True

    End Sub

    Private Sub cmdShell_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdShell.Click
        If OsShell.StartsWith("explorer") Then
            Shell(OsShell, AppWinStyle.MaximizedFocus)
        ElseIf OsShell.Equals("cmd.exe") Or OsShell.Equals("command.exe") Then
            Directory.SetCurrentDirectory(UserDirName)
            Shell(OsShell, AppWinStyle.MaximizedFocus)
        End If
    End Sub

    Private Sub cmdOptions_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdOptions.Click
        frmOptions.Visible = True
    End Sub

    Private Sub cmdSettings_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdSettings.Click
        frmsettings.Visible = True

    End Sub

    Private Sub mnuOptions_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles mnuOptions.Click
        frmOptions.Visible = True

    End Sub

    Private Sub mnuDirectory_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles mnuDirectory.Click
        frmChangeDir.Visible = True

    End Sub

    Private Sub mnuSettings_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles mnuSettings.Click
        frmsettings.Visible = True

    End Sub

    Private Sub mnuCommandLine_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles mnuCommandLine.Click
        Directory.SetCurrentDirectory(UserDirName)
        Shell("cmd.exe", AppWinStyle.NormalFocus)
    End Sub


    Private Sub notifymenu_Popup(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles notifymenu.Popup

    End Sub

    Private Sub cmuStopAllDs_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmuStopAllDs.Click
        Shell(DPPEJDirName + "\bin\" + "stop_all_daemons.bat", AppWinStyle.NormalFocus)
    End Sub

    Private Sub cmnuCmdLine_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmnuCmdLine.Click
        Shell(OsShell, AppWinStyle.MaximizedFocus)
    End Sub

    Private Sub cmuExplorer_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmuExplorer.Click
        Shell("explorer " + UserDirName)

    End Sub

    Private Sub cmnuOptions_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmnuOptions.Click
        frmOptions.Show()

    End Sub

    Private Sub cmnuSettings_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmnuSettings.Click
        frmsettings.Show()

    End Sub

    Private Sub cmnuExit_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmnuExit.Click
        Me.Close()
    End Sub


    Private Sub cboNoClients_SelectedValueChanged(ByVal sender As Object, ByVal e As System.EventArgs) Handles cboNoClients.SelectedValueChanged
        noClients = cboNoClients.SelectedValue
    End Sub

    Private Sub mnuReadme_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles mnuReadme.Click

    End Sub


    Private Sub cboNoClients_TextChanged(ByVal sender As Object, ByVal e As System.EventArgs) Handles cboNoClients.TextChanged
        If cboNoClients.Text = "" Then
            cboNoClients.Text = 1
        End If
        noClients = Integer.Parse(cboNoClients.Text)
    End Sub

    Private Sub mnuProjectDoc_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles mnuProjectDoc.Click

    End Sub



   
    Private Sub Timer1_Tick(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Timer1.Tick
        frmSplash.Close()
        Me.Show()
        Timer1.Enabled() = False

    End Sub
End Class
