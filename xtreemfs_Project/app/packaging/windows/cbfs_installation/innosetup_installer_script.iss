; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "XtreemFS Windows Client"
#define MyAppVersion "1.6"
#define MyAppPublisher "XtreemFS Project"
#define MyAppURL "http://www.XtreemFS.org/"
#define MyAppExeName "run_xtreemfs_client_command_line_prompt.bat"

#define GitRoot "..\..\..\"
#define CbFSAdapterBuildDir GitRoot + "cpp\build\Release\"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{EA8FA8CB-02C9-4028-8CBC-C109F9B8DFFA}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
DisableProgramGroupPage=yes
LicenseFile=LICENSE_WIN32
OutputBaseFilename=xtreemfs_windows_client_{#MyAppVersion}
SetupIconFile=xtreemfs_logo_transparent.ico
Compression=lzma
;Compression=none
SolidCompression=yes
InfoBeforeFile=info_before_install.txt

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

; Entries in Startmenu Folder
[Icons]
Name: "{group}\Run XtreemFS Client (command line prompt)"; Filename: "{app}\{#MyAppExeName}"; WorkingDir: "{app}"; IconFilename: "{app}\xtreemfs_logo_transparent.ico"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"

[Files]
; Binaries
Source: "{#CbFSAdapterBuildDir}mount.xtreemfs.exe"; DestDir: "{app}"; Flags: ignoreversion; BeforeInstall: InstallCbFS
;Source: "{#CbFSAdapterBuildDir}mount.xtreemfs.pdb"; DestDir: "{app}"; Flags: ignoreversion;
Source: "{#CbFSAdapterBuildDir}mkfs.xtreemfs.exe"; DestDir: "{app}"; Flags: ignoreversion;
Source: "{#CbFSAdapterBuildDir}rmfs.xtreemfs.exe"; DestDir: "{app}"; Flags: ignoreversion;
Source: "{#CbFSAdapterBuildDir}lsfs.xtreemfs.exe"; DestDir: "{app}"; Flags: ignoreversion;
; Required OpenSSL libraries
Source: "{#CbFSAdapterBuildDir}libeay32.dll"; DestDir: "{app}"; Flags: ignoreversion;
Source: "{#CbFSAdapterBuildDir}ssleay32.dll"; DestDir: "{app}"; Flags: ignoreversion;
; CbFS driver and install helper
Source: "cbfsinst.dll"; DestDir: "{app}";
Source: "cbfs.cab"; DestDir: "{app}";
; Instructions and command line prompt script to simply running the client
Source: "readme.txt"; DestDir: "{app}"; 
Source: "{#MyAppExeName}"; DestDir: "{app}"; 
Source: "xtreemfs_logo_transparent.ico"; DestDir: "{app}"; 
; Visual C++ Redistributables 2015
Source: "vcredist_x86_2015.exe"; DestDir: {tmp}; Flags: deleteafterinstall
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Run]
Filename: "{tmp}\vcredist_x86_2015.exe"; Parameters: "/q";
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[Code]
var
  RebootNeeded: DWORD;
  UninstallRebootNeeded: DWORD;

// Add install dir to PATH environment variable.
const 
    ModPathName = 'modifypath'; 
    ModPathType = 'system'; 

function ModPathDir(): TArrayOfString; 
begin 
    setArrayLength(Result, 1) 
    Result[0] := ExpandConstant('{app}'); 
end; 
#include "modpath.iss"
  
#IFDEF UNICODE
  #DEFINE AW "W"
#ELSE
  #DEFINE AW "A"
#ENDIF

function GetLastError: Integer;
 external 'GetLastError@Kernel32.dll stdcall';  

// Install CbFS
function RunCbFSInstaller(CabPathName: String; ProductName: String; InstallPath: String; SupportPnP: Integer; ModulesToInstall: DWORD; var RebootNeeded: DWORD) : Integer;
  // use cbfs.cab here so it gets extracted into {tmp} during setup
  external 'Install{#AW}@files:cbfsinst.dll,cbfs.cab stdcall setuponly';

procedure InstallCbFS();
var
  InstallerResult: Integer;
  LastError: Integer;
begin
  Log('Using CbFS archive: ' + ExpandConstant('{tmp}\cbfs.cab'));
  // Use AppId as ProductName, same as in cbfs_adapter.cpp
  // Empty InstallPath means install to Windows system folders.
  // 196608 in decimal is 0x00010000 | 0x00020000, see cbfsinst.h for the flags.
  // The other two flags are return codes in RebootNeeded to tell which module
  // caused the need for a reboot.
  InstallerResult := RunCbFSInstaller(ExpandConstant('{tmp}\cbfs.cab'), 'EA8FA8CB-02C9-4028-8CBC-C109F9B8DFFA', '', 1, 196608, RebootNeeded);
  if InstallerResult <> 1 then
  begin
    LastError := GetLastError() 
    MsgBox('Failed to install the CbFS driver (GetLastError returned ' + IntToStr(LastError) + '). Please reboot your system and try again.', mbCriticalError, MB_OK);
  end;
end;

function NeedRestart(): Boolean;
begin
  If RebootNeeded <> 0 then
    Result := True
  else
    Result := False;
end;

// Uninstall CbFS
function RunCbFSUninstaller(CabPathName, ProductName: String; InstalledPath: String; var RebootNeeded: DWORD) : Integer;
external 'Uninstall{#AW}@{app}\cbfsinst.dll stdcall uninstallonly';

procedure UninstallCbFS();
var
  InstallerResult: Integer;
  LastError: Integer;
begin
  Log('Using CbFS archive: ' + ExpandConstant('{app}\cbfs.cab'));
  InstallerResult := RunCbFSUninstaller(ExpandConstant('{app}\cbfs.cab'), 'EA8FA8CB-02C9-4028-8CBC-C109F9B8DFFA', '', RebootNeeded);
  if InstallerResult <> 1 then
  begin
    LastError := GetLastError()
    MsgBox('Failed to uninstall the CbFS driver (GetLastError returned ' + IntToStr(LastError) + '.', mbCriticalError, MB_OK);
  end;
end;

procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
begin
  If CurUninstallStep = usUninstall Then
  begin
    UninstallCbFS()
    UnloadDLL(ExpandConstant('{app}\cbfsinst.dll'))
  end;

  CurUninstallStepChangedModPath(CurUninstallStep)
end;

function UninstallNeedRestart(): Boolean;
begin
  If UninstallRebootNeeded <> 0 then
    Result := True
  else
    Result := False;
end;

//[Tasks]
//  Name: modifypath; Description: Add application directory to your environmental path; Flags: unchecked