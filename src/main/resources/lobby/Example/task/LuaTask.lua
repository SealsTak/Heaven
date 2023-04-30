System.out:println("LuaTask Load!")

function lua(args)
    System.out:print("LuaTask Hello World! ")
    System.out:println(args)
    thisLua:getLobby():getScriptTasks():get("JavaScriptTask"):callFunction("js", { "23333J from L" })
end